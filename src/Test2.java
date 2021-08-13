import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author qcw
 * @date 2021/8/8 13:38
 */
public class Test2 {

    public static void main(String[] args) throws Exception{

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(8888);
        serverSocketChannel.socket().bind(inetSocketAddress);
        //接收监听端口号的消息
        SocketChannel accept = serverSocketChannel.accept();
        ByteBuffer buffer = ByteBuffer.allocate(10);
        while (true){
            int read = 0;
            while (read != -1){
                read = accept.read(buffer);
                buffer.flip();

                System.out.println("本次监听到的内容是 --- "+ new String(buffer.array()));
                //buffer.flip();
                buffer.clear();
            }
        }
    }

}
