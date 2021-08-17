package groupchat;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * @author qcw
 * @date 2021/8/11 22:50
 */
public class Server {

    //选择器
    Selector selector;

    //服务器端监听 channel
    ServerSocketChannel listenChannel;

    // listen 端口号
    public static final int port = 8888;

    //服务器初始化
    public Server(){
        try {
            this.selector = Selector.open();
            this.listenChannel = ServerSocketChannel.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
            this.listenChannel.configureBlocking(false);
            this.listenChannel.socket().bind(inetSocketAddress);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void listen(){

        while (true){

            try {

                //监听客户端事件
                int count = selector.select(2000);
                if(count > 0){

                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()){

                        SelectionKey key = iterator.next();
                        //监听到客户端连接事件
                        if(key.isAcceptable()){
                            //获取连接的channel
                            SocketChannel socketChannel = listenChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector,SelectionKey.OP_READ);
                            SocketAddress remoteAddress = socketChannel.getRemoteAddress();
                            System.out.println("用户 -- " + remoteAddress + "已上线");
                        }
                        //监听到读取事件
                        if(key.isReadable()){

                            this.readMsg(key);

                        }
                        //移除处理过的channel，防止重复处理
                        iterator.remove();
                    }

                }
                else {
                    //System.out.println("等待客户端连接...");
                }
            }catch (Exception e){
                    e.printStackTrace();
            }


        }

    }

    //读取事件
    public synchronized void readMsg(SelectionKey key){
        SocketChannel socketChannel = null;
        try{
            socketChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(5);
            String content = "";
            int read = socketChannel.read(buffer);
            while (read != 0 && read !=-1){
                buffer.flip();
                //System.out.println("非数组打印: ");
                while (buffer.hasRemaining()){
                    //System.out.print(buffer.get());
                    content = content + (char)buffer.get();
                }
                buffer.clear();
                //content = content + new String(buffer.array());
                //System.out.println("数组打印: "+ Arrays.toString(buffer.array()));
                read = socketChannel.read(buffer);
            }

            if(!"test-connceted".equals(content)){
                System.out.println("用户 -- "+socketChannel.getRemoteAddress()+" 说: "+content);
                this.sendMsgToOthers("用户 -- "+socketChannel.getRemoteAddress()+"说: "+content,socketChannel);
            }

        }catch (Exception e){
            //捕获异常
            e.printStackTrace();
            try {
                System.out.println("客户端 -- "+socketChannel.getRemoteAddress()+" 已下线");
                //注销key
                key.cancel();
                //关闭通道
                socketChannel.close();
            }catch (Exception e2){
               e2.printStackTrace();
            }

        }

    }


    //转发消息到其他客户端
    public void sendMsgToOthers(String content, SocketChannel self) throws Exception{

        Set<SelectionKey> keys = selector.keys();
        Iterator<SelectionKey> iterator = keys.iterator();
        while (iterator.hasNext()){
            SelectionKey key = iterator.next();
            //跳过服务器自身
            if(key.channel() instanceof ServerSocketChannel){
                continue;
            }
            SocketChannel channel = (SocketChannel) key.channel();
            //排除客户端自身
            if(self.equals(channel)){
                continue;
            }
            //发送消息给其他客户端
            channel.write(ByteBuffer.wrap(content.getBytes()));
            //这里不能移除元素,因为这不是判断客户端的事件，而是服务器主动发送消息给客户端
            //iterator.remove();
        }

    }


    public void sendMsgToOthersTest(String content) throws Exception{

        Set<SelectionKey> keys = selector.keys();
        Iterator<SelectionKey> iterator = keys.iterator();
        while (iterator.hasNext()){
            SelectionKey key = iterator.next();
            //跳过服务器自身
            if(key.channel() instanceof ServerSocketChannel){
                continue;
            }
            SocketChannel channel = (SocketChannel) key.channel();
            //发送消息给其他客户端
            channel.write(ByteBuffer.wrap(content.getBytes()));
        }


    }

    public static void sendToOthersThread(Server server){
        new Thread(()->{
            //服务器发送消息测试
            while (true){
                try {
                    server.sendMsgToOthersTest("服务器发送消息到客户端测试");
                    Thread.sleep(2000);
                    System.out.println("服务器发送消息成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public static void main(String[] args) throws Exception {

        Server server = new Server();
        sendToOthersThread(server);
        
        server.listen();

    }


}
