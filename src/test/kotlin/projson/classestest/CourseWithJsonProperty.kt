package projson.classestest

import projson.annotation.JsonIgnore
import projson.annotation.JsonProperty

data class CourseWithJsonProperty(
    @JsonProperty("desc")
    val description: String,

    @JsonIgnore
    val deadline: String?,

    @JsonProperty("deps")
    val dependencies: List<String>
)