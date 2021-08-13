import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author qcw
 * @date 2021/8/7 19:41
 */
public class Main {

    public static void main(String[] args) throws Exception {

        FileInputStream inputStream = new FileInputStream("C:\\Users\\root\\Desktop\\test.txt");

        FileChannel channel = inputStream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(5);
        channel.read(buffer);
        System.out.println(buffer);
        //buffer.put("222".getBytes());
        buffer.flip();
        StringBuilder str = new StringBuilder("");
        while (buffer.hasRemaining()){
            str.append(String.valueOf(buffer.get()));
        }

        channel.write(buffer);

    }

}
