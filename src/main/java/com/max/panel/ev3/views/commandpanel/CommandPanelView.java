package com.max.panel.ev3.views.commandpanel;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.max.panel.ev3.views.MainLayout;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Command Panel")
@Route(value = "panel", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class CommandPanelView extends HorizontalLayout {

    private TextField name;
    private Button sayHello;

    public CommandPanelView() {
        setMargin(true);
        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        add(name, sayHello);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
    }

}
