package projson.classestest

import projson.annotation.JsonProperty

data class WithReservedDollarId(
    @JsonProperty("\$id") val name: String
)

data class WithReservedDollarRef(
    @JsonProperty("\$ref") val name: String
)

data class WithReservedDollarType(
    @JsonProperty("\$type") val name: String
)

data class WithReservedId(
    @JsonProperty("id") val name: String
)

data class WithReservedType(
    @JsonProperty("type") val name: String
)

data class WithEmptyJsonProperty(
    @JsonProperty("") val name: String
)

data class WithBlankJsonProperty(
    @JsonProperty("   ") val name: String
)
