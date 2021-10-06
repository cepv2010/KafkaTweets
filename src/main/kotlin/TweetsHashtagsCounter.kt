import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KeyValueMapper
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.kstream.ValueMapper
import java.util.*


class TweetsHashtagsCounter

var objectMapper = ObjectMapper()

fun main(args: Array<String>) {
    val props = Properties()
    props[StreamsConfig.APPLICATION_ID_CONFIG] = "tweethashtagscounterappkk1"
    props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
    props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
    props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name

    val builder = StreamsBuilder()
    val tweets: KStream<String, String> = builder.stream<String, String>(TOPIC_NAME)

    tweets.flatMapValues(ValueMapper<String, List<String?>> { value: String? ->
        listOf(
            getHashtags(
                value
            )
        )
    })
        .groupBy(KeyValueMapper { _: Any?, value: Any? -> value })
        .count()
        .toStream()
        .print(Printed.toSysOut<Any, Long>())

    val topology = builder.build()
    val streams = KafkaStreams(topology, props)
    streams.start()

}

fun getHashtags(input: String?): String? {
    val root: JsonNode
    try {
        root = objectMapper.readTree(input)
        val hashtagsNode = root.path("entities").path("hashtags")
        if (hashtagsNode.toString() != "[]") {
            return hashtagsNode[0].path("text").asText()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}
