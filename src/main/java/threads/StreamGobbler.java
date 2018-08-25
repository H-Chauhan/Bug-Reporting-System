package threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class StreamGobbler extends Thread
{
    private InputStream is;
    private String type;

    StreamGobbler(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }

    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            while (br.readLine() != null);
        } catch (IOException ioe)	{
            ioe.printStackTrace();
        }
    }
}
