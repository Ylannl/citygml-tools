/*
 * citygml-tools - Collection of tools for processing CityGML files
 * https://github.com/citygml4j/citygml-tools
 *
 * citygml-tools is part of the citygml4j project
 *
 * Copyright 2018-2022 Claus Nagel <claus.nagel@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citygml4j.tools.option;

import org.citygml4j.cityjson.model.CityJSONVersion;
import org.citygml4j.cityjson.writer.OutputEncoding;
import picocli.CommandLine;

import java.util.Locale;

public class CityJSONOutputOptions implements Option {
    @CommandLine.Option(names = {"-v", "--cityjson-version"}, defaultValue = "1.1",
            description = "CityJSON version to use for output file(s): 1.1, 1.0 (default: ${DEFAULT-VALUE}).")
    private String version;

    @CommandLine.Option(names = "--write-cityjson-features",
            description = "Create CityJSONFeature objects and write them as JSON Lines to enable streaming. " +
                    "This option can only be used with CityJSON 1.1.")
    private boolean writeCityJSONFeatures;

    @CommandLine.Option(names = "--output-encoding", defaultValue = "UTF-8",
            description = "Encoding to use for output file(s): UTF-8, UTF-16, UTF-32 (default: ${DEFAULT-VALUE}).")
    private String encoding;

    @CommandLine.Option(names = "--pretty-print",
            description = "Format and indent output file(s).")
    private boolean prettyPrint;

    @CommandLine.Option(names = "--html-safe",
            description = "Write JSON that is safe to embed into HTML.")
    private boolean htmlSafe;

    private CityJSONVersion versionOption;
    private OutputEncoding encodingOption;

    public CityJSONVersion getVersion() {
        return versionOption;
    }

    public boolean isWriteCityJSONFeatures() {
        return writeCityJSONFeatures;
    }

    public OutputEncoding getEncoding() {
        return encodingOption;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public boolean isHtmlSafe() {
        return htmlSafe;
    }

    @Override
    public void preprocess(CommandLine commandLine) {
        switch (version) {
            case "1.0":
                versionOption = CityJSONVersion.v1_0;
                break;
            case "1.1":
                versionOption = CityJSONVersion.v1_1;
                break;
            default:
                throw new CommandLine.ParameterException(commandLine,
                        "Invalid value for option '--cityjson-version': expected one of [1.1, 1.0] " +
                                "but was '" + version + "'");
        }

        if (writeCityJSONFeatures) {
            if (getVersion() != CityJSONVersion.v1_1) {
                throw new CommandLine.ParameterException(commandLine,
                        "Error: --write-cityjson-features can only be used with CityJSON version 1.1");
            } else if (prettyPrint) {
                throw new CommandLine.ParameterException(commandLine,
                        "Error: --write-cityjson-features and --pretty-print are mutually exclusive (specify only one)");
            }
        }

        switch (encoding.toUpperCase(Locale.ROOT)) {
            case "UTF-8":
                encodingOption = OutputEncoding.UTF8;
                break;
            case "UTF-16":
                encodingOption = OutputEncoding.UTF16_LE;
                break;
            case "UTF-32":
                encodingOption = OutputEncoding.UTF32_LE;
                break;
            default:
                throw new CommandLine.ParameterException(commandLine,
                        "Invalid value for option '--output-encoding': expected one of [UTF-8, UTF-16, UTF-32] " +
                                "(case-insensitive) but was '" + encoding + "'");
        }
    }
}
