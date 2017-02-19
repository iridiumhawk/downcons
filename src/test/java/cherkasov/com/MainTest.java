package cherkasov.com;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;

//full test
public class MainTest {
    private final String[] argsAllFullCorrect = {"-n", "5", "-l", "500000", "-f", "links.txt", "-o", "output"};


    @Before
    public void initTest(){
    }

    @Test
    public void testMainArgsFullCorrect() throws Exception {
        Main.main(argsAllFullCorrect);
    }


    /*@Test
    @Ignore
    public void saveFile(){
        long time = 0;
        String fileName = Paths.get("output", "test.temp").toString();
        String fileIn = Paths.get("output", "idea").toString();

        try (
                InputStream in = new FileInputStream(fileIn);
                FileOutputStream fos = new FileOutputStream(fileName)
        ){


            byte[] buf = new byte[100000];

            int numBytesRead;

            while (true) {
                numBytesRead = in.read(buf);
                if (numBytesRead == -1) {
                    break;
                }

                Long timer = System.nanoTime();

//                    numBytesRead = (int) fos.getChannel().transferFrom(rbc, bytesDownloaded, inputBufferOneThread);
//                fos.flush();

                fos.write(buf, 0, numBytesRead);


                timer = System.nanoTime() - timer;

                if (time > 0 ) {
                    time  = (time + timer) / 2;
                } else {
                    time = timer;
                }
            }

            System.out.println("average time, nanosec - "+ time);

        } catch (IOException e){

        }

    }*/

}