package com.star.auto;
// Import log4j classes.
import com.star.foo.Bar;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MyApp {

    // Define a static logger variable so that it references the
    // Logger instance named "MyApp".
    private static final Logger logger = LogManager.getLogger(MyApp.class);

    public static void main(final String... args) {

        // Set up a simple configuration that logs on the console.
        //输出如此多的信息时为了测试RollingFileAppender的日志文件自动生成是否配置正确。
        logger.trace("Entering application.Entering application.Entering application.Entering application." +
                "Entering application.Entering application.Entering application.Entering application." +
                "Entering application.Entering application.Entering application.Entering application." +
                "Entering application.Entering application.Entering application.Entering application." +
                "Entering application.Entering application.Entering application.Entering application." +
                "Entering application.Entering application.Entering application.Entering application." +
                "Entering application.Entering application.Entering application.Entering application." +
                "Entering application.Entering application.Entering application.Entering application."
                );

        Bar bar = new Bar();
        if (!bar.doIt()) {
            logger.error("Didn't do it.");
        }

        logger.trace("Exiting application.");
    }
}