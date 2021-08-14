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

    InetSocketAddress inetSocketAddress = new InetSocketAddress(IP, PORT);

    public Client() throws Exception {
        selector = Selector.open();
        socketChannel = SocketChannel.open(inetSocketAddress);
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

    //判断服务器的连接状态
    public static boolean isConnected(Client client){

        try {
            //client.socketChannel.connect(client.inetSocketAddress);
            client.socketChannel.finishConnect();
            return true;
        } catch (IOException e) {
            return false;
        }

    }


    public static void main(String[] args) throws Exception {

        Client client = new Client();

        //开启一个线程监听服务器，如果服务器上线，就和服务器端重新建立连接
        new Thread(()->{
            //服务器发送消息测试
            while (true){
                try {
                    //如果检测到断开连接，那就试图重新与服务器建立连接
                    if(Client.isConnected(client)) {
                        System.out.println("断开连接,正在尝试重新连接到服务器...");
                        //首先关闭之前的连接
                        client.socketChannel.close();
                        client.socketChannel = null;
                        //重新建立连接
                        client.socketChannel = SocketChannel.open(client.inetSocketAddress);
                        client.socketChannel.configureBlocking(false);
                        client.socketChannel.register(client.selector,SelectionKey.OP_READ);
                        System.out.println("重连成功");
                    }
                    Thread.sleep(2000);
                    System.out.println("连接状态是 : "+client.socketChannel.finishConnect());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();

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
