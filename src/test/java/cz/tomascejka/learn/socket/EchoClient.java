package cz.tomascejka.learn.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.tomascejka.learn.socket.channel.Channel;
import cz.tomascejka.learn.socket.channel.ChannelStrategyException;
import cz.tomascejka.learn.socket.channel.impl.ChannelSocketCloseByFinAckPacket;
import cz.tomascejka.learn.socket.channel.impl.Configuration;

public class EchoClient 
{
	private static final Logger LOG = LoggerFactory.getLogger(EchoClient.class);
	
	public static void main(String[] args) throws Exception 
	{
		String logPrefix = "["+UUID.randomUUID().toString()+"]";
		Channel<String,String> channel = new ChannelSocketCloseByFinAckPacket(new Configuration(), logPrefix);
		
		// input will be console
		BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
		try 
		{
			// establish connection
			channel.connect();
			
			LOG.info("{} Type (\"Bye.\" to quit)", logPrefix);
			String userInput;
			while ((userInput = inputStream.readLine()) != null) 
			{
				// send data to server/receive response from server
				String response = channel.sendAndRecieve(logPrefix+";"+userInput);
				LOG.info("{} echo: {}", logPrefix, response);
				// end loop
				if (userInput.equals("Bye.")) 
				{
					LOG.info("{} Client sent: quit command ...", logPrefix);
					break;
				}
			}
		}
		catch (Exception e) 
		{
			throw new RuntimeException(logPrefix+"Problem with communication exchange",e);
		}
		finally 
		{
			closeResources(channel, inputStream, logPrefix);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static void closeResources(Channel channel, BufferedReader stdIn, String logPrefix) 
			throws ChannelStrategyException
	{
		// close connection
		channel.close();
		// close up-level buffer
		if(stdIn != null)
		{
			try 
			{
				stdIn.close();
				stdIn = null;
			} 
			catch (IOException e) 
			{
				LOG.error(logPrefix+" Problem with closing bufferedReader", e);
			}
		}		
	}
}
