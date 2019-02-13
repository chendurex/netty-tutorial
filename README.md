# netty-tutorial
#### Channel
  + 整个网络操作以及配置等的核心，相当于整个网络交互的顶级抽象，内部又细分为下面几个子模块
  + ChannelFuture
    + 所有的操作都是异步的，而且会返回一个ChannelFuture，用户通过ChannelFuture来确定当前请求是否成功等
    + 当操作完成事件触发后，还可以通过增加事件的方式再次触发其它的业务操作
  + ChannelPipeline
   + 以类似过滤器的方式实现数据流的控制，用户可以在pipeline中添加handler(比如encode、decode)，
      handler之间会通过HandlerContext进行传播
  + ChannelConfig
   + 所有与操作有关的配置 
  + ChannelHandler  
   + 实现数据的加工处理，包括但不限于数据编解码、业务逻辑处理,多个ChannelHandler之间通过调用ChannelPipeline.addLast
     方法将它们构建成一个个ChannelHandlerContext对象，然后组装成一个链表，最后通过ChannelHandlerContext进行驱动
  + ChannelHandlerContext
   + ChannelHandler上下文环境控制器，用于控制数据的写入以及触发下一个ChannelHandler
  + 与filter类比
    + ChannelConfig == FilterConfig
    + ChannelHandler == Filter
    + ChannelHandlerContext == FilterChain
    + ChannelPipeline == Filter Functions