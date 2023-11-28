package chatting2;
import javax.swing.JFrame;
public class Application {
	public static void main(String[] args) {
        // TODO code application logic here
        
        Client client1 = new Client();
        client1.setVisible(true);
        client1.setSize(1300,575);
        client1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Client client2 = new Client();
        client2.setVisible(true);
        client2.setSize(1300, 575);
        client2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Client client3 = new Client();
        client3.setVisible(true);
        client3.setSize(1300, 575);
        client3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Server Server = new Server();
        Server.setVisible(true);
        Server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
