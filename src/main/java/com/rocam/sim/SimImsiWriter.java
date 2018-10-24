package com.rocam.sim;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author trovo.st@gmail.com
 * 2018-10-20
 */
public class SimImsiWriter
{

	public static void main(String[] args) throws CardException, IOException
	{
		TerminalFactory tf = TerminalFactory.getDefault();
		List<CardTerminal> terminals = tf.terminals().list();
		System.out.println("Available Readers:");
		System.out.println(terminals + "\n");

		CardTerminal cardTerminal = terminals.get(0);

		Card connection = cardTerminal.connect("*");
		CardChannel cardChannel = connection.getBasicChannel();

		ResponseAPDU responseApdu;

		responseApdu = cardChannel.transmit(new CommandAPDU(0xA0, 0xA4, 0x00, 0x00, new byte[] { 0x3F, 0x00 }));
		System.out.println(DatatypeConverter.printHexBinary(responseApdu.getBytes()));
		responseApdu = cardChannel.transmit(new CommandAPDU(0xA0, 0xF2, 0x00, 0x00, 0x16));
		System.out.println(DatatypeConverter.printHexBinary(responseApdu.getBytes()));
		responseApdu = cardChannel.transmit(new CommandAPDU(0xA0, 0xA4, 0x00, 0x00, new byte[] { 0x7F, 0x20 }));
		System.out.println(DatatypeConverter.printHexBinary(responseApdu.getBytes()));
		responseApdu = cardChannel.transmit(new CommandAPDU(0xA0, 0xA4, 0x00, 0x00, new byte[] { 0x6F, 0x07 }));
		System.out.println(DatatypeConverter.printHexBinary(responseApdu.getBytes()));

		responseApdu = cardChannel.transmit(new CommandAPDU(0xA0, 0x20, 0x00, 0x02,
			new byte[] { //0x08,
				0x36, 0x33, 0x35, 0x34,
				(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF }));

		System.out.println(DatatypeConverter.printHexBinary(responseApdu.getBytes()));

		responseApdu = cardChannel.transmit(new CommandAPDU(0xA0, 0xD6, 0x00, 0x00, DatatypeConverter.parseHexBinary("09082923016400843998")));
		byte[] imsi = responseApdu.getBytes();
		System.out.print(DatatypeConverter.printHexBinary(imsi));



		/*System.out.print(DatatypeConverter.printHexBinary(new byte[]{(byte)((imsi[1] & 0xF0) >> 4)}));
		for (int i = 2; i < imsi.length; i++)
		{
			System.out.print(DatatypeConverter.printHexBinary(new byte[]{swapNibbles(imsi[i])}));
		}*/

		System.out.println();

		connection.disconnect(true);
	}

	static byte swapNibbles(byte x)
	{
		return (byte)((x & 0x0F) << 4 | (x & 0xF0) >> 4);
	}
}
