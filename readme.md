http://ifeve.com/netty5-user-guide/
http://netty.io/wiki/user-guide.html

Java1.4以前问题
1. 无数据缓冲区，存在IO性能问题
2. 无C/C++中的channel概念，只有stream
3. 同步阻塞BIO，通常导致通信被长时间阻塞
4. 支持字符集有限，硬件移植性不好

Linux 将所有外部设备看作一个文件来操作，对一个文件读写会调用内核提供的系统命令，返回一个file descriptor 文件描述符
而对一个socket也有相应的描述符，socketfd，描述符就是一个数字，指向内核中的一个结构体（文件路径，数据区等一些属性）

《UNIX网络编程》

Unix对IO模型分类
1. 阻塞型

    以socket为例，在进程空间调用recvfrom，其系统调用直到数据包到达且被复制到应用进程缓冲区中或发生错误时才返回，在此期间会一直等待
    ![](images/IO%20block.png)
2. 非阻塞型

    应用层recvfrom到内核时，如果数据缓冲区没有数据，就直接返回一个EWOULDBLOCK错误，一般都对非阻塞型IO模型进行轮询这个状态，看内核
    是否有数据到来
    ![](images/IO%20non%20block.png)
    
3. 复用模型
    
    Linux提供select/poll，进程通过将一个或多个fd传递给select或poll系统调用，阻塞在select上，这样select/poll可以帮我们
    侦测多个fd是否处于就绪状态。select/poll是顺序扫描fd是否就绪，而且支持的fd数量有限，因此它的使用受到了制约。
    Linux还提供了epoll系统调用，epoll基于事件驱动方式替代顺序扫描，因此性能更高。当有fd准备就绪时，立即回调函数rollback。
    ![](images/IO%20replicator.png)
    
4. 信号驱动型

    首先开启套接口信号驱动IO功能，并通过系统调用sigaction执行一个信号处理函数（此系统调用立即返回，进程继续工作，它是非阻塞的）
    当数据准备就绪时，就为该进程生成一个sigio信号,通过信号回调通知应用程序调用recvfrom来读取数据，并通知主循环函数处理数据
    
    ![](images/IO%20sigal.png)
    
5. 异步IO
    
    告知内核启动某个操作，并让内核在完成整个操作后（包括将数据从内核复制到用户的缓冲区）通知我们。这种模型与信号驱动模型主要的
    区别在于：信号驱动IO由内核通知我们何时开始一个IO操作；异步模型由内核通知我们IO操作何时已完成
    
    ![](images/IO%20asyn.png)
    
五种模型对比

   ![](images/IO%20compare.png)
   
   Java NIO的核心类库多路复用器Selector就是基于epoll技术实现的
   
epoll优势
1. 支持一个进程打开的socket fd不受限制（仅受限于操作系统的最大文件句柄数）
    select的fd_size默认为1024，但epoll在1G机器上大约10w
    
    cat /proc/sys/fs/file-max 
2. IO 效率不会随着fd数目的增加而线性下降
3. 使用mmap加速内核用户空间的消息传递
4. epoll的api更简单

