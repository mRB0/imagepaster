package imagepaster;

import java.applet.Applet;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;
import java.util.*;
import java.text.*;


public class Imagepaster extends Applet implements ActionListener {

	static final long serialVersionUID = 1012021;
	
	Button mabutton;
	Button uploadbutton;
	Label malabel;
	Imagebox gpanel;
	
	Image my_image;
	
	public void init()
	{
		LayoutManager lm = new BorderLayout();

		this.setSize(600, 600);
		this.mabutton = new Button("Paste");
		this.uploadbutton = new Button("Upload");
		this.uploadbutton.setEnabled(false);
		
		this.malabel = new Label("Paste an image mmkay");
		this.gpanel = new Imagebox();
		
		BorderLayout outerlm = new BorderLayout();
		
		Panel innerpnl = new Panel();
		innerpnl.setLayout(lm);
		
		innerpnl.add(this.mabutton, BorderLayout.NORTH);
		innerpnl.add(this.uploadbutton, BorderLayout.SOUTH);
		innerpnl.add(this.gpanel, BorderLayout.CENTER);
		
		this.mabutton.addActionListener(this);
		this.uploadbutton.addActionListener(this);
		
		this.setLayout(outerlm);
		this.add(innerpnl, BorderLayout.CENTER);
		this.add(this.malabel, BorderLayout.SOUTH);
		
	}
	
	public void actionPerformed(ActionEvent e) throws HeadlessException
	{
		if (e.getSource() == this.mabutton)
		{
			try
			{
				Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
				
				Transferable contents = cb.getContents(null);
				if (contents == null || !contents.isDataFlavorSupported(DataFlavor.imageFlavor))
				{
					this.malabel.setText("Bad flavor");
					return;
				}
				//System.out.println("Tasty");
				my_image = (Image) contents.getTransferData(DataFlavor.imageFlavor);
				
				this.gpanel.setImage(my_image);
				this.gpanel.repaint();
				this.uploadbutton.setEnabled(true);
				this.malabel.setText("Tasty paste");
			}
			catch(IOException ioe)
			{
				System.out.println("IO Exception");
			}
			catch(UnsupportedFlavorException ufe)
			{
				System.out.println("UFE");
			}
		}
		else if (e.getSource() == this.uploadbutton)
		{
			this.upload();
		}
	}
	
	String boundary = "Aa0Aa033";
	
	public void upload()
	{
		if (this.my_image == null)
		{
			return;
		}
		
		try
		{
			this.malabel.setText("Uploading...");
			
//			URL u = new URL("http://imgur.com/processUpload1.php");
//			URL u = new URL("http://s.engramstudio.com/upload.cgi");
			URL u = new URL("http://snobwall.com/upload/upload.cgi");
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			
			huc.setDoInput(true);
			huc.setDoOutput(true);
			huc.setUseCaches(false);
			huc.setRequestProperty(
					"Connection",
					"Keep-Alive");
			huc.setRequestProperty(
					"HTTP_REFERER",
					"http://snobwall.com/upload/upload.cgi");
			huc.setRequestProperty(
					"Content-Type",
					"multipart/form-data; boundary=" + boundary);
					
			huc.setRequestMethod("POST");
			
			DataOutputStream dstream = new DataOutputStream(huc.getOutputStream());
			
			/*
			dstream.writeBytes("--" + boundary + "\r\n");
			dstream.writeBytes("Content-disposition: form-data; name=\"MAX_FILE_SIZE\"\r\n\r\n");
			dstream.writeBytes("10485760");
			
			dstream.writeBytes("--" + boundary + "\r\n");
			dstream.writeBytes("Content-disposition: form-data; name=\"UPLOAD_IDENTIFIER\"\r\n\r\n");
			dstream.writeBytes("hexlehexle");
			*/
			
			Date d = new Date();
			SimpleDateFormat df = new SimpleDateFormat();
			df.applyPattern("yyyy-MM-dd_HHmmss");
			String the_date = df.format(d);
			
			
			dstream.writeBytes("--" + boundary + "\r\n");
			dstream.writeBytes("Content-Disposition: form-data; name=\"uploadfile\"; filename=\"clip_" + the_date + ".png\"\r\n");
			dstream.writeBytes("Content-Type: image/png\r\n\r\n");
			
			/*
			File fst = new File("C:\\move0.png");
			
			dstream.writeBytes("Content-Length: " + fst.length() + "\r\n\r\n");
			
			FileInputStream fin = new FileInputStream("C:\\move0.png");
			
			byte[] buf = new byte[1024];
			int bread;
			while(-1 != (bread=fin.read(buf, 0, 1024)))
			{
				dstream.write(buf, 0, bread);
			}
			*/
			ImageIO.write((BufferedImage)this.my_image, "png", dstream);
			
			dstream.writeBytes("\r\n--" + boundary + "--\r\n");
			
			huc.connect();
			
			System.out.println(huc.getResponseMessage());
			if (huc.getResponseCode() != 200)
			{
				this.malabel.setText(huc.getResponseCode() + huc.getResponseMessage());
			}
			else
			{
				BufferedReader bis = new BufferedReader(new InputStreamReader(huc.getInputStream()));
				String line;
				while ((line = bis.readLine()) != null)
				{
					System.out.println(line);
				}
				this.malabel.setText("Upload complete and maybe successful!");
				this.getAppletContext().showDocument(new URL("http://snobwall.com/upload/stored/clip_" + the_date + ".png"));
			}
			
		}
		catch(MalformedURLException mue)
		{	
			System.out.println("mue: " + mue.getMessage());
			this.malabel.setText("mue: " + mue.getMessage());
		}
		catch(IOException ioe)
		{
			System.out.println("ioe: " + ioe.getMessage());
			this.malabel.setText("ioe: " + ioe.getMessage());
		}
	}
	
	public void stop()
	{
	}
}
