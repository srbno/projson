package projson.classestest

data class Playlist(
    val playlistId: String,
    val tracks: List<Track>,
    val genres: Set<String>,
    val attributes: Map<String, String>
)
