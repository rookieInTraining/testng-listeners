package com.rookieintraining.listeners;

import com.rookieintraining.services.ConsoleService;
import com.rookieintraining.services.ITestNGService;
import com.rookieintraining.services.UIReportingService;

import java.util.List;

public class ReportSampleListener extends BaseTestNGListener {

    public static final List<ITestNGService> iTestNgServices;

    static {
        iTestNgServices = List.of(
                new UIReportingService(),
                new ConsoleService()
        );
    }

    public ReportSampleListener() {
        super(iTestNgServices);
    }

}
