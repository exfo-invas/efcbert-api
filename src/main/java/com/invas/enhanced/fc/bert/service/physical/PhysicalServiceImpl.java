package com.invas.enhanced.fc.bert.service.physical;

import com.invas.enhanced.fc.bert.model.physical.PhysicalStatus;
import com.invas.enhanced.fc.bert.utils.ScpiCommandConstants;
import org.springframework.stereotype.Service;

@Service
public class PhysicalServiceImpl implements PhysicalService {

    ScpiCommandConstants scpiCommandConstants = new ScpiCommandConstants();


    @Override
    public PhysicalStatus getStatus() {



        //Execute command to get status
        return new PhysicalStatus();
    }
}
