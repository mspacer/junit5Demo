package com.msp.junit;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TagFilter;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.PrintWriter;

public class TestLauncher {

    public static void main(String[] args) {
        // дефолтный лаунчер DefaultLauncher
        Launcher launcher = LauncherFactory.create();

        //собирает и регистрирует тесты на основе выбранных параметров
        LauncherDiscoveryRequest launcherRequest = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectPackage("com.msp.junit"))
                .filters(TagFilter.includeTags("login"))
                .build();

        //launcher.execute возвращает Void и ничего не сообщает о результает выполниея
        // для сбора статистики существует листенер
        SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();
        launcher.execute(launcherRequest, summaryGeneratingListener);

        try (PrintWriter printWriter = new PrintWriter(System.out)) {
            summaryGeneratingListener.getSummary().printTo(printWriter);
        }
    }
}
