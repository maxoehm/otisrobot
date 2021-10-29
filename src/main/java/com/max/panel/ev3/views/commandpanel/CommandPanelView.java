package com.max.panel.ev3.views.commandpanel;

import com.max.panel.ev3.Application;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.max.panel.ev3.views.MainLayout;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.boot.SpringApplication;
import org.vaadin.artur.helpers.LaunchUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

@PageTitle("Command Panel")
@Route(value = "panel", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class CommandPanelView extends HorizontalLayout {

    private TextField command;
    private Button commitCommand;

    public CommandPanelView() {
        {


            setMargin(true);

            TextField response = new TextField("Server Response");
            response.setReadOnly(true);

            command = new TextField("Your command");
            commitCommand = new Button("Send");
            add(command, commitCommand, response);
            setVerticalComponentAlignment(Alignment.END, command, commitCommand);
            commitCommand.addClickListener(e -> {

                try {
                    response.setValue(sendCommand());
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

            });
        }

    }

    private String sendCommand() throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException  {
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        //establish socket connection to server
        socket = new Socket(host.getHostName(), 9876);
        //write to socket using ObjectOutputStream
        oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Sending request to Socket Server");


        oos.writeObject(command);
        //read the server response message
        ois = new ObjectInputStream(socket.getInputStream());
        String message = (String) ois.readObject();
        //close resources
        ois.close();
        oos.close();

        return message;
    }


}