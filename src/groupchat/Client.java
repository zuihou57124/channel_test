package groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author qcw
 * @date 2021/8/12 19:53
 */
public class Client {

    public static final String IP = "127.0.0.1";

    public static final int PORT = 8888;

    Selector selector;

    SocketChannel socketChannel;


    public Client() throws Exception {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(IP, PORT));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    //发送消息到服务器
    public void send(String str){
        try {
            ByteBuffer buffer = ByteBuffer.allocate(20);
            buffer.put(str.getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            //System.out.print(" , 发送成功");
        }catch (Exception e){
            //System.out.print(" , 发送失败");
            e.printStackTrace();
        }
    }

    public  void read(){

        try{
            int select = selector.select(2000);
            if(select > 0){
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(35);
                        channel.read(buffer);
                        System.out.println(new String(buffer.array()));
                    }
                    iterator.remove();
                }
            }
            else {

            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws Exception {

        Client client = new Client();

//        new Thread(){
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        client.read();
//                        //每隔两秒读取一次服务器发送的信息
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();

        int sum = 0;
        while (true) {
            try {
                //client.send(String.valueOf(++sum));
                Thread.sleep(2000);
                client.read();
                //每隔两秒读取一次服务器发送的信息
                System.out.println("我说: "+sum);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }





        //Scanner scanner = new Scanner(System.in);
        //client.send("测试发消息");

        //client.send(String.valueOf(1));
//        client.send(String.valueOf(2));
//        client.send(String.valueOf(3));

//        int sum = 0;
//        while (true){
//            sum++;
//            Thread.sleep(2000);
//            client.send(String.valueOf(sum));
//        }

    }


}
