package com.company.homeworkloans.screen.client;

import com.company.homeworkloans.entity.Client;
import com.company.homeworkloans.screen.requestloan.RequestLoan;
import io.jmix.ui.Screens;
import io.jmix.ui.component.Button;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("Client.browse")
@UiDescriptor("client-browse.xml")
@LookupComponent("clientsTable")
public class ClientBrowse extends StandardLookup<Client> {
    @Autowired
    private Screens screens;

    private void showRequestLoanScreen() {
        screens.create(RequestLoan.class).show();
    }

    @Subscribe("requestLoanBtn")
    public void onRequestLoanBtnClick(Button.ClickEvent event) {
        showRequestLoanScreen();
    }
}