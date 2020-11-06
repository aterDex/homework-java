package ru.otus.homework.hw32.common.tcp;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TcpTest {

    @Test
    public void Test() throws Exception {

//        ByteArrayOutputStream baos = new ByteArrayOutputStream(40000);
//
//        ObjectOutputStream oos = new ObjectOutputStream(baos);
//
//        oos.writeObject(new Signal("AAA", null));
//        oos.writeObject(new Signal("BBB", null));
//
//        byte[] ba = baos.toByteArray();
//        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
//
//        ObjectInputStream isss = new ObjectInputStream(bais);
//
//        Object obj = isss.readObject();
//        Object obj2 = isss.readObject();


        ExecutorService ex = Executors.newSingleThreadExecutor();

        SignalTcpServer sts = new SignalTcpServer("127.0.0.1", 12000);
        Future<?> ft = ex.submit(sts);
        Thread.sleep(2000);
        SignalTcpClient stc = new SignalTcpClient("127.0.0.1", 12000, x -> {
        });
        stc.run();
    }
}
