package core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Visualisation extends JPanel {
	
	private static final long serialVersionUID = -2552767259864949497L;
	private BufferedImage bg_img;
	private BufferedImage[] c_img = new BufferedImage[5];
	private BufferedImage[] b_img = new BufferedImage[5];
	private int visu_width = 800, visu_height = 600;
	private Font ui_font;
	
	public Visualisation(){
		loadSprites();
		
		try {
		     ui_font = Font.createFont(Font.TRUETYPE_FONT, new File("kenvector_future.ttf")).deriveFont(18.0f);
		} catch (IOException|FontFormatException e) {
		     //Handle exception
		}
		
		JFrame f = new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.setSize( 800, 639 );
		f.setResizable(false);
		f.setTitle("Barbershop - Java 8 Streams - Vorlesung Betriebsysteme/Uni Trier");
		f.add( this );
		
		// position the frame in the middle of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		f.setLocation((screenSize.width/2)-(visu_width/2),(screenSize.height/2)-(visu_height/2));
		f.setVisible( false );
	}
	
	private void vpaint( Graphics g )
	{
		g.setColor( Color.WHITE ); 
		g.fillRect( 0, 0, getWidth(), getHeight() );
		g.drawImage(bg_img, 0, 0, 800,	600, this);
		
		// draw customers
		for(int i=0; i<M.CUSTOMERS; i++){
			Customer c = (Customer)M.p.get(i);
			g.setFont(ui_font);
			g.setColor(Color.BLACK);
			g.drawString(c.getName(), c.x+28, c.y);
			g.drawImage(c_img[c.state], c.x, c.y, this);
		}
		// draw barbers
		for(int i=0; i<M.BARBERS; i++){
			Barber b = (Barber)M.p.get(i+M.CUSTOMERS);
			g.drawString(b.name, b.x+28, b.y);
			g.drawImage(b_img[b.sprite_state], b.x, b.y, this);
		}
		g.setColor(Color.decode("0x749d09"));
		g.drawString("Money earned £"+M.cash,500,500);
	}

	private final GraphicsConfiguration gfxConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

	private BufferedImage offImg;
	@Override protected void paintComponent( Graphics g ){
		if ( offImg == null || offImg.getWidth() != getWidth() || offImg.getHeight() != getHeight() ){
			offImg = gfxConf.createCompatibleImage( getWidth(), getHeight() );
			vpaint( offImg.createGraphics() );
		}
		
		g.drawImage( offImg, 0, 0, this );
		vpaint(g);
	}
	private void loadSprites(){
		bg_img = null;
		try {
		    bg_img = ImageIO.read(new File("barbershop.png"));
		    // customer sprites
		    c_img[0] = ImageIO.read(new File("c_0.png"));
		    c_img[1] = ImageIO.read(new File("c_1.png"));
		    c_img[2] = ImageIO.read(new File("c_2.png"));
		    c_img[3] = ImageIO.read(new File("c_3.png"));
		    // barber sprites
		    b_img[0] = ImageIO.read(new File("b_0.png"));
		    b_img[1] = ImageIO.read(new File("b_1.png"));
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void update(){
		repaint();
	}
}