@echo off
call mvnw dependency:build-classpath -Dmdep.outputFile=target\classpath -Dmdep.regenerateFile=true -q
<NUL set /p=-cp target\classes;> target\cmdargs
type target\classpath >> target\cmdargs
title SimpleV
java @target\cmdargs --add-opens java.base/java.lang=ALL-UNNAMED --add-opens=java.compiler/javax.tools=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED simplev.Main %*