package koval.proxyseller.twitter.dto

import groovy.transform.TupleConstructor
import io.swagger.v3.oas.annotations.media.Schema

@TupleConstructor
@Schema(description = "Paginated response wrapper")
class PageResponseDto<T> {
    @Schema(description = "List of items in the current page")
    List<T> content

    @Schema(description = "Current page number (0-indexed)", example = "0")
    int page

    @Schema(description = "Page size", example = "20")
    int size

    @Schema(description = "Total number of elements", example = "100")
    long totalElements

    @Schema(description = "Total number of pages", example = "5")
    int totalPages

    @Schema(description = "Whether this is the first page", example = "true")
    boolean first

    @Schema(description = "Whether this is the last page", example = "false")
    boolean last
}
