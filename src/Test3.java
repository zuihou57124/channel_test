import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author qcw
 * @date 2021/8/8 13:46
 */
public class Test3 {

    public static void main(String[] args) throws Exception{

        SocketAddress socketAddress = new InetSocketAddress(8888);
        SocketChannel channel = SocketChannel.open(socketAddress);
        int sum = 0;
        while (true){
            Thread.sleep(2000);
            sum++;
            channel.write(ByteBuffer.wrap(("你好--" + sum).getBytes()));
        }


    }

}
