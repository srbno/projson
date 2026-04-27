package projson

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProJsonTestCenario2 {
    @Test
    fun testPlaylist() {
        val playlist = Playlist(
            playlistId = "pl-001",
            tracks = listOf(
                Track(title = "Great I Am", duration = 3.5),
                Track(title = "Thank you Jesus", duration = 4.2)
            ),
            genres = setOf("Chill", "Christian"),
            attributes = mapOf(
                "createdBy" to "user123",
                "mood" to "relaxed"
            )
        )

        val json = ProJson().toJsonObject(playlist)

        assertEquals(
            $$"""{"$type":"Playlist","attributes":{"createdBy":"user123","mood":"relaxed"},"genres":["Chill","Christian"],"playlistId":"pl-001","tracks":[{"$type":"Track","duration":3.5,"title":"Great I Am"},{"$type":"Track","duration":4.2,"title":"Thank you Jesus"}]}""",
            json
        )

    }
}

data class Playlist(
    val playlistId: String,
    val tracks: List<Track>,
    val genres: Set<String>,
    val attributes: Map<String, String>
)

data class Track(
    val title: String,
    val duration: Double
)