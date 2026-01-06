java --add-opens java.base/java.lang=ALL-UNNAMED ^
     --add-opens java.base/sun.net.util=ALL-UNNAMED ^
     --add-opens java.xml/com.sun.xml.internal.bind.v2.runtime.reflect=ALL-UNNAMED ^
     -Djava.net.preferIPv4Stack=true ^
-Xmx1024m -classpath ./scouter-server-boot.jar scouter.boot.Boot ./lib
