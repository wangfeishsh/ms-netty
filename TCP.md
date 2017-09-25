[TCP的几个重要参数介绍](http://blog.csdn.net/abc86319253/article/details/50751734)

1. TCP_NODELAYED

默认情况下, TCP发送数据采用Nagle算法.。Nagle算法是解决小数据的频繁发送问题，比如1个字节的数据，
在封包后会加上几十字节的首部，相当浪费资源。Nagle的做法是发送方发送的数据不会立即发出，
而是先放在缓冲区,等待缓冲区达到一定的大小，或者是缓冲达到一定的时间后再一批发出。 
发送完一批数据后, 会等待接收方对这批数据的回应，然后再发送下一批数据.。
Negle算法适用于发送方需要发送大批量数据, 并且接收方会及时作出回应的场合, 
这种算法通过减少传输数据的次数来提高通信效率。

如果发送方持续地发送小批量的数据, 并且接收方不一定会立即发送响应数据, 那么Negle算法会使发送方运行很慢。
对于GUI 程序, 如网络游戏程序(服务器需要实时跟踪客户端鼠标的移动), 这个问题尤其突出.。
客户端鼠标位置改动的信息需要实时发送到服务器上,由于Negle算法采用缓冲, 大大减低了实时响应速度, 
导致客户程序运行很慢。

我们可以通过设置TCP_NODELAYED来禁用Negle算法。

2. so_keepalive

so_keepalive是TCP的心跳机制，保持连接检测对方主机是否崩溃，避免（服务器）永远阻塞于TCP连接的输入。
设置该选项后，如果2小时内在此套接口的任一方向都没有数据交换，
TCP就自动给对方 发一个保持存活探测分节(keepalive probe)。
这是一个对方必须响应的TCP分节。它会导致以下三种情况：

* 对方接收一切正常：以期望的ACK响应，2小时后，TCP将发出另一个探测分节。

* 对方已崩溃且已重新启动：以RST响应。套接口的待处理错误被置为ECONNRESET，套接 口本身则被关闭。

* 对方无任何响应：源自berkeley的TCP发送另外8个探测分节，相隔75秒一个，试图得到一个响应。
在发出第一个探测分节11分钟15秒后若仍无响应就放弃。套接口的待处理错误被置为ETIMEOUT，套接口本身则被关闭。
如ICMP错误是“host unreachable(主机不可达)”，说明对方主机并没有崩溃，但是不可达，
这种情况下待处理错误被置为 EHOSTUNREACH。

SO_KEEPALIVE有三个参数，其详细解释如下:

* tcp_keepalive_intvl，保活探测消息的发送频率。默认值为75s。
发送频率tcp_keepalive_intvl乘以发送次数tcp_keepalive_probes，
就得到了从开始探测直到放弃探测确定连接断开的时间，大约为11min。

* tcp_keepalive_probes，TCP发送保活探测消息以确定连接是否已断开的次数。默认值为9（次）。
值得注意的是，只有设置了SO_KEEPALIVE套接口选项后才会发送保活探测消息。

* tcp_keepalive_time，在TCP保活打开的情况下，最后一次数据交换到TCP发送第一个保活探测消息的时间，
即允许的持续空闲时间。默认值为7200s（2h）。

3. backlog

对于TCP连接，内核维护两个队列： 
1. 未完成连接的队列，此队列维护着那些已收到了客户端SYN分节信息，等待完成三路握手的连接，
socket的状态是SYN_RCVD。 
2. 已完成的连接的队列，此队列包含了那些已经完成三路握手的连接，socket的状态是ESTABLISHED，但是等待accept。

backlog

backlog在linux2.2之后表示队列2（已经完成连接，等待accept调用）。

backlog的值太小会导致在大量连接的时候不能处理，丢弃客户端发送的ack，此时如果客户端认为连接建立继续发送数据，
就会出现满请求。backlog过大会导致连接积压，性能下降。

调用listen监听的时候可以设置backlog的值，然backlog 并不是按照你调用listen的所设置的backlog大小，
实际上取的是backlog和somaxconn的最小值。somaxconn的值定义在/proc/sys/net/core/somaxconn，默认是128，
可以把这个值修改更大以满足高负载需求。

syn_backlog

syn_backlog指队列1（半连接SYN_RCVD阶段）。

这个值在/proc/sys/net/ipv4/tcp_max_syn_backlog ，可以对其进行调整。
但是一般情况下处于syn_rcvd阶段的不会太多，除非遇到SYN_FLOOD攻击。

SYN FLOOD：

    SYN Flood利用的是TCP协议缺陷，发送大量伪造的TCP连接请求，从而使得被攻击方资源耗尽（CPU满负荷或内存不足）
    的攻击方式。在被攻击主机用netstat可以看见80端口存在大量的半连接状态(SYN_RECV)，
    用tcpdump抓包可以看见大量伪造IP发来的SYN连接，S也不断回复SYN+ACK给对方，
    可惜对方并不存在(如果存在则S会收到RST这样就失去效果了)，所以会超时重传。
    这个时候如果有正常客户A请求S的80端口，它的SYN包就被S丢弃了，因为半连接队列已经满了，达到攻击目的。