import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import twitter4j.*
import twitter4j.conf.ConfigurationBuilder
import java.util.*


class ProducerTweets

const val TOPIC_NAME = "rawtweets"

fun main(args: Array<String>) {
    val apiKey = args[0]
    val apiSecret = args[1]
    val tokenValue = args[2]
    val tokenSecret = args[3]

    val props = Properties()
    props["acks"] = "1"
    props["retries"] = 3
    props["batch.size"] = 16384
    props["buffer.memory"] = 33554432
    props["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
    props["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
    props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"

    val prod = KafkaProducer<String, String>(props)
    val cb = ConfigurationBuilder()
    cb.setOAuthAccessToken(tokenValue)
    cb.setOAuthAccessTokenSecret(tokenSecret)
    cb.setOAuthConsumerKey(apiKey)
    cb.setOAuthConsumerSecret(apiSecret)
    cb.setJSONStoreEnabled(true)
    cb.setIncludeEntitiesEnabled(true)

    val twitterStream = TwitterStreamFactory(cb.build()).instance;

    try {
        val listenerEx: StatusListener = object : StatusListener {
            override fun onStatus(status: Status) {
                val hashtags: Array<HashtagEntity> = status.getHashtagEntities()
                if (hashtags.isNotEmpty()) {
                    val value = TwitterObjectFactory.getRawJSON(status)
                    val lang: String = status.getLang()
                    prod.send(ProducerRecord(TOPIC_NAME, lang, value))
                }
            }

            override fun onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
            override fun onTrackLimitationNotice(i: Int) {}
            override fun onScrubGeo(l: Long, l1: Long) {}
            override fun onStallWarning(stallWarning: StallWarning) {}
            override fun onException(e: Exception) {}
        }
        twitterStream.addListener(listenerEx)
        twitterStream.sample()
    }catch (e: java.lang.Exception){
        e.printStackTrace()
    }

}