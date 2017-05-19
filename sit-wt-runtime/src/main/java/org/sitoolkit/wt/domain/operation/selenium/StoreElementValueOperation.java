/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sitoolkit.wt.domain.operation.selenium;

import javax.annotation.Resource;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.tester.TestContext;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.stereotype.Component;

/**
 * 要素の属性値を変数に格納する操作です。
 *
 * @author hiroyuki.takeda
 */
@Component
public class StoreElementValueOperation extends SeleniumOperation {

    @Resource
    TestContext context;

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {

        String strValue = null;
        Point locValue = null;
        Rectangle rectValue = null;
        Dimension dimValue = null;

        String name = testStep.getValue();
        String getType[] = testStep.getDataType().split(":");
        WebElement element = findElement(testStep.getLocator());

        switch (getType[0]) {

            case "text":
                strValue = element.getText();
                break;
            case "tag":
                strValue = element.getTagName();
                break;
            case "attribute":
                if (1 < getType.length)
                    strValue = element.getAttribute(getType[1]);
                break;
            case "css":
                if (1 < getType.length)
                    strValue = element.getCssValue(getType[1]);
                break;
            case "location":
                locValue = element.getLocation();
                break;
            case "rect":
                rectValue = element.getRect();
                break;
            case "size":
                dimValue = element.getSize();
                break;
        }

        if (strValue != null) {
            log.info("変数を定義します {}={}", name, strValue);
            context.addParam(name, strValue);
        } else if (locValue != null) {
            log.info("変数を定義します {}={}", name, locValue);
            context.addParam(name, locValue);
        } else if (rectValue != null) {
            log.info("変数を定義します {}={}", name, rectValue);
            context.addParam(name, rectValue);
        } else if (dimValue != null) {
            log.info("変数を定義します {}={}", name, dimValue);
            context.addParam(name, dimValue);
        }
    }
}
