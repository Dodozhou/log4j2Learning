# log4j2Learning
log4j2配置测试小例子，基于SpringBoot、Maven构建。包含控制台和文件输出，使用最常用的Patter Layout格式化输出。附详解教程。

log4j2官方文档地址：http://logging.apache.org/log4j/2.x/manual/configuration.html

### log4j2有4中配置方式  
- 通过配置文件配置
- 创建一个配置工厂来配置
- 调用拓展配置API来向默认配置添加容器等
- 调用内部logger的方法

### log4j2可以通过四种配置文件配置：  
- log4j.properties、log4j2.properties、log4j2-test.properties.
- log4j.json、log4j2.json、log4j2-test.json.
- log4j.yaml、log4j2.yaml、log4j2-test.yaml.
- log4j.xml、log4j2.xml、log4j2-test.xml.


### 自动配置
若没有找到配置文件，那么log4j2将使用默认配置，日志将被输出到控制台。  
默认的配置使用ConsoleAppender为root logger，PatternLayout为输出类型，具体为："%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"  
默认的输出等级为Level.ERROR

```
import com.foo.Bar;
 
// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
 
public class MyApp {
 
    // Define a static logger variable so that it references the
    // Logger instance named "MyApp".
    private static final Logger logger = LogManager.getLogger(MyApp.class);
 
    public static void main(final String... args) {
 
        // Set up a simple configuration that logs on the console.
 
        logger.trace("Entering application.");
        Bar bar = new Bar();
        if (!bar.doIt()) {
            logger.error("Didn't do it.");
        }
        logger.trace("Exiting application.");
    }
}
```

### 自动重加载功能
当通过一个文件加载配置的时候，Log4j可以自动识别配置文件发生变化，并根据变化重新加载。如果 configuration 的属性monitorInterval 被设置了一个非0的值，那么Log4j2会周期性的检查配置文件。  
接下来的例子展示了每隔30秒进行一次检测的例子。（最小的时间间隔是5秒，不能小于5）。
```
<?xml version="1.0" encoding="UTF-8"?>
    <Configuration monitorInterval="30">
    ...
    </Configuration>
```

### 配置
通常的配置文件：
```
<?xml version="1.0" encoding="UTF-8"?>;
  <Configuration>
    <Properties>
     <Property name="name1">value</property>
     <Property name="name2" value="value2"/>
   </Properties>
   <Filter type="type" ... />
   <Appenders>
     <Appender type="type" name="name">
       <PatternLayout pattern="%m MDC%X%n"/>
       <Filter type="type" ... />
     </Appender>
    ...
   </Appenders>
   <Loggers>
     <Logger name="name1">
       <Filter type="type" ... />
     </Logger>
     ...
     <Root level="level">
       <AppenderRef ref="name"/>
     </Root>
   </Loggers>
 </Configuration>
```
#### 一、配置Configuration
Configuration标签的重要属性

1. status：表示控制台应该打印的状态日志等级。可选的值按顺序有：“trace”、“debug”、“info”、“warn”、“error”、“fatal”
```
    <Configuration status="WARN">
```
2. name：配置的名称
3. monitorInterval：自动重加载的时间间隔，最短5秒，见上例。
4. dest：状态日志输出目的地。值为”err",a file path，URL 3者之一。如果为“err”，信息会被输出到stderr。

#### 二、配置loggers
log4j2使用logger元素来配置日志记录器。下面是
##### logger的重要属性：

1. name：独有的名字，用于和其他logger区分。
2. level：规定这个logger记录日志的最低级别，可选值为：TRACE、DEBUG、INFO、WARN、ERROR、ALL、OFF。如果该属性没有被配置，那么ERROR将会是默认级别。
3. addivity：规定日志是否会因为父logger而重复记录。布尔类型，true或者false，默认是false。

##### logger的注意事项：

1. LoggerConfig也可以使用一个或多个AppenderRef引用元素进行配置。如果在一个LoggerConfig中配置多个appenders，当处理日志事件的时候，每个appender都会被调用。

2. **每一个configuration都必须有一个root  logger。**如果没有配置，一个默认的root LoggerConfig就会被使用，它的级别为ERROR并且有一个Console的appender。

    1. rootlogger没有名字
    2. 因为root logger没有父元素，所以rootlogger不支持additivity属性。

#### 三、配置Appenders
appender要么是通过使用一个指定的appender插件的名称进行配置，要么是通过一个appender元素并且type属性的值为appender插件的名称进行配置。如：
```
    <Appenders>
        <Appender type="Console" name="MyConsoleAppender">
        </Appender>
        <!--或者-->
        <Console name="STDOUT">
        </Console>
    </Appenders>

```
此外每一个appender一定要指定一个在appenders集合中独一无二的name属性。这个name属性会被logger的AppenderRef引用。  
大多数的appenders也支持配置layout(layout规定是日志输出格式的属性)，layout可以通过指定Layout插件的名称进行配置，也可以通过使用“layout”作为元素名并为其指定type属性（type属性的值为layout插件的名称）
```
     <Console name="STDOUT">
      <PatternLayout pattern="%m MDC%X%n"/>
    </Console>
    <!--或者-->
     <Console name="STDOUT">
      <Layout type="PatternLayout" pattern="%m MDC%X%n"/>
    </Console>
```
各种各样的appender将包含他们的正常功能所需的特有属性或是元素。

#### 四、配置Filters（过滤器）
filter用来过滤特定日志事件，判断决定接受或者拒接处理该日志事件。  
Log4j允许在4个位置配置过滤器元素：  

1. 和appenders，loggers，properties元素在同一个级别。这些过滤器在日志事件传递给LoggerConfig之前对日志事件进行接受或者是拒绝
2. 在一个logger元素中。为指定的logger接受和拒绝日志事件
3. 在一个appender中。阻止或者导致日志事件被appender处理
4. 在一个AppenderRef元素中。用来判断一个日志记录器是有应该将日志事件传递到appender中

filters是一个集合，可以在filters元素中配置任意个 filter 元素。

```
<File name="File" fileName="${filename}">
       <!-- this pattern outputs class name and line number -->
       <PatternLayout pattern="%C{1}.%M %m %ex%n"/>
       <filters>
         <MarkerFilter marker="FLOW" onMatch="ACCEPT" onMismatch="NEUTRAL"/>
         <MarkerFilter marker="EXCEPTION" onMatch="ACCEPT" onMismatch="DENY"/>
       </filters>
</File>

<Logger name="org.apache.logging.log4j.test1" level="debug" additivity="false">
       <ThreadContextMapFilter>
         <KeyValuePair key="test" value="123"/>
       </ThreadContextMapFilter>
       <AppenderRef ref="STDOUT"/>
</Logger>

```

### .properties配置方式
```
status = error
dest = err
name = PropertiesConfig
 
property.filename = target/rolling/rollingtest.log
 
filter.threshold.type = ThresholdFilter
filter.threshold.level = debug
 
appender.console.type = Console
appender.console.name = STDOUT
appender.console.layout.type = PatternLayout
appender.console.layout.pattern = %m%n
appender.console.filter.threshold.type = ThresholdFilter
appender.console.filter.threshold.level = error
 
appender.rolling.type = RollingFile
appender.rolling.name = RollingFile
appender.rolling.fileName = ${filename}
appender.rolling.filePattern = target/rolling2/test1-%d{MM-dd-yy-HH-mm-ss}-%i.log.gz
appender.rolling.layout.type = PatternLayout
appender.rolling.layout.pattern = %d %p %C{1.} [%t] %m%n
appender.rolling.policies.type = Policies
appender.rolling.policies.time.type = TimeBasedTriggeringPolicy
appender.rolling.policies.time.interval = 2
appender.rolling.policies.time.modulate = true
appender.rolling.policies.size.type = SizeBasedTriggeringPolicy
appender.rolling.policies.size.size=100MB
appender.rolling.strategy.type = DefaultRolloverStrategy
appender.rolling.strategy.max = 5
 
logger.rolling.name = com.example.my.app
logger.rolling.level = debug
logger.rolling.additivity = false
logger.rolling.appenderRef.rolling.ref = RollingFile
 
rootLogger.level = info
rootLogger.appenderRef.stdout.ref = STDOUT
```
注意，该properties配置文件的语法与log4j 1.x中的不同。与其他配置文件的方式相同，properties同样使用组件以及组件的属性来配置log4j2.  
在2.6版本以前，类似于log4j 1，appenders,filters，loggers等属性的标识符（就是他们的名字）都必须用一个“，”分隔的列表列出来。下面调用时都使用该标识符加“.”的方式。但在2.6版本之后，不用再列出这些标识符列表，这些标识符在第一次使用时会被自动推断出来。不过，如果你要使用复杂的标识符时，仍然必须使用列表列出。

### XInclude
XML格式的配置文件可以通过XInclude引入其他文件。下面是log4j2.xml引入其他两个xml文件的例子：
```
<?xml version="1.0" encoding="UTF-8"?>
    <configuration xmlns:xi="http://www.w3.org/2001/XInclude"
               status="warn" name="XIncludeDemo">   <!--引入XIclude的命名空间-->
    <properties>
    <property name="filename">xinclude-demo.log</property>
    </properties>
     <ThresholdFilter level="debug"/>
    <xi:include href="log4j-xinclude-appenders.xml" /> <!--引入文件-->
    <xi:include href="log4j-xinclude-loggers.xml" />
</configuration>
```
log4j-xinclude-appenders.xml文件内容如下:
```
<?xml version="1.0" encoding="UTF-8"?>
<appenders>
  <Console name="STDOUT">
    <PatternLayout pattern="%m%n" />
  </Console>
  <File name="File" fileName="${filename}" bufferedIO="true" immediateFlush="true">
    <PatternLayout>
      <pattern>%d %p %C{1.} [%t] %m%n</pattern>
    </PatternLayout>
  </File>
</appenders>
```
log4j-xinclude-loggers.xml文件内容如下:
```
<?xml version="1.0" encoding="UTF-8"?>
<loggers>
  <logger name="org.apache.logging.log4j.test1" level="debug" additivity="false">
    <ThreadContextMapFilter>
      <KeyValuePair key="test" value="123" />
    </ThreadContextMapFilter>
    <AppenderRef ref="STDOUT" />
  </logger>
 
  <logger name="org.apache.logging.log4j.test2" level="debug" additivity="false">
    <AppenderRef ref="File" />
  </logger>
 
  <root level="error">
    <AppenderRef ref="STDOUT" />
  </root>
</loggers>
```
### 复合配置
log4j允许多个配置文件同时被使用。他们将被一个实现了MergeStrategy接口的类来合并组织。默认的合并配置中，同样的property、Filters、Scripts、Appenders、Loggers，后面的配置文件会覆盖前面的相同配置。
### 状态日志
log4j和普通程序一样也需要调试。当log4j的配置还没有加载时，外部日志无法使用。为了解决这个缺陷，我们可以使用Log4j2 API中的StatusLogger。它的声明方法如下：
```
    private final static Logger logger=StatusLogger.getLogger();
```
因为StatusLogger实现了Logger接口，因此所有普通日志的操作方法，StatusLogger也可以使用。StatusLogger的记录等级可以通过两种方式配置：

1. 通过```<Configuration status="debug">```配置
2. 使用系统变量“Log4jDefaultStatusLevel”的初始值。

可选的值有：tarce、debug、info、warn、error和fatal。  
StatusLogger配置过后，会在控制台打印响应级别的log4j初始化的事件日志，如果我们把status设为“error”，那么我们就可以检查log4j配置中的错误了。举例如下：  
有错的配置文件：
```
    <Appenders>
    <Console name="STDOUT">
      <PatternLayout pattern="%m%n"/>
      <ThresholdFilter level="debug"/>
    </Console>
    <Routing name="Routing">
      <Routes pattern="$${sd:type}">
        <Route>
          <RollingFile name="Rolling-${sd:type}" fileName="${filename}"
                       filePattern="target/rolling1/test1-${sd:type}.%i.log.gz">
            <PatternLayout>
              <pattern>%d %p %c{1.} [%t] %m%n</pattern>
            </PatternLayout>
            <SizeBasedTriggeringPolicy size="500" />
          </RollingFile>
        </Route>
        <Route ref="STDOUT" key="Audit"/>
      </Routes>
    </Routing>
  </Appenders>

    <logger name="EventLogger" level="info" additivity="false">
    <AppenderRef ref="Routng"/>   <!--错误在这里，"Routng"少了一个i-->
    </logger>
```
将status属性改为error后运行，控制台会打印如下语句：
```
    2017-11-8 23:21:25,517 ERROR Unable to locate appender Routng for logger EventLogger
```
如果我们想把状态日志输出到其他地方，可以通过设置Configuration的dest属性：
```
    <Configuration status="error" dest="./log/log.file">
```
除此之外，我们还可以通过编码的方式来配置这些属性：
```
    StatusConsoleListener listener = new StatusConsoleListener(Level.ERROR);
    StatusLogger.getLogger().registerListener(listener);
```

如果觉得不错，请点个赞鼓励鼓励:-D

更多：
[Log4j2官方文档翻译、学习笔记之二——Appender的分类及常用类型示例](http://blog.csdn.net/zhoucheng05_13/article/details/78494458)  
[Log4j2官方文档翻译、学习笔记之三——Layouts的分类及常用类型示例](http://blog.csdn.net/zhoucheng05_13/article/details/78569661)  

