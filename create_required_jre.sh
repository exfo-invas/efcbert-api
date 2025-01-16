#!/bin/bash

# Create required JRE
#jlink --module-path "$JAVA_HOME/jmods;libs" --add-modules java.base,java.desktop,java.instrument,java.management,java.naming,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.sql,jdk.compiler,jdk.jfr,jdk.unsupported --output EnchancedFCBertJRE

#jdeps --print-module-deps --ignore-missing-deps target/FCBert-0.0.1-SNAPSHOT.jar


#jpackage --input target/ --main-jar FCBert-0.0.1-SNAPSHOT.jar --name EnhancedFCBERT --type jar --runtime-image EnchancedFCBertJRE --app-version 1.0 --description "Spring Boot with Embedded Java" --vendor "Invas"
