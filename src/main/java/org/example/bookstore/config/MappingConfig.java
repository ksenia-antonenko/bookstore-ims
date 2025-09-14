package org.example.bookstore.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * Interface that represents mapping configuration.
 *
 * @see <a href="https://mapstruct.org/documentation/stable/reference/html/#defining-mapper">Defining a mapper</a>
 * @see <a href="https://mapstruct.org/documentation/stable/reference/html/#retrieving-mapper">Retrieving a mapper</a>
 * @see <a href="https://mapstruct.org/documentation/stable/reference/html/#datatype-conversions">Data type conversions</a>
 * @see <a href="https://mapstruct.org/documentation/stable/reference/html/#mapping-collections">Mapping collections</a>
 * @see <a href="https://mapstruct.org/documentation/stable/reference/html/#_advanced_mapping_options">Advanced mapping options</a>
 * @see <a href="https://mapstruct.org/documentation/stable/reference/html/#_reusing_mapping_configurations">Reusing mapping configurations</a>
 */
@MapperConfig(componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    typeConversionPolicy = ReportingPolicy.WARN)
public class MappingConfig {
}
