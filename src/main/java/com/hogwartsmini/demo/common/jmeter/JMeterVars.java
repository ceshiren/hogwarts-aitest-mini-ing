package com.hogwartsmini.demo.common.jmeter;

import io.github.ningyu.jmeter.plugin.dubbo.sample.DubboSample;
import org.apache.jmeter.extractor.RegexExtractor;
import org.apache.jmeter.extractor.XPath2Extractor;
import org.apache.jmeter.extractor.json.jsonpath.JSONPostProcessor;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.jdbc.sampler.JDBCSampler;
import org.apache.jorphan.collections.HashTree;

import java.util.StringJoiner;

/**
 *  JSR223 控件是 代替 BeanShell 的新一代脚本控件，
 *  支持多种脚本语言，尤其是其中的 Groovy，
 *  更是重点推荐使用的脚本语言。
 *
 *   vars 只能在当前线程内使用，props 可以跨线程组使用
 *   vars 只能保存 String 或者 Object，props 是 Hashtable 对象
 */
@Deprecated
public class JMeterVars {

    private JMeterVars() {
    }

    /**
     * 处理所有请求，有提取变量的请求增加后置脚本提取变量值
     *
     * @param tree
     */
    public static void addJSR223PostProcessor(HashTree tree) {
        for (Object key : tree.keySet()) {
            HashTree node = tree.get(key);
            if (key instanceof HTTPSamplerProxy || key instanceof DubboSample || key instanceof JDBCSampler) {
                StringJoiner extract = new StringJoiner(";");
                for (Object child : node.keySet()) {
                    if (child instanceof RegexExtractor) {
                        RegexExtractor regexExtractor = (RegexExtractor) child;
                        extract.add(regexExtractor.getRefName());
                    } else if (child instanceof XPath2Extractor) {
                        XPath2Extractor regexExtractor = (XPath2Extractor) child;
                        extract.add(regexExtractor.getRefName());
                    } else if (child instanceof JSONPostProcessor) {
                        JSONPostProcessor regexExtractor = (JSONPostProcessor) child;
                        extract.add(regexExtractor.getRefNames());
                    }
                }

                /*if (Optional.ofNullable(extract).orElse(extract).length() > 0) {
                    JSR223PostProcessor shell = new JSR223PostProcessor();
                    shell.setEnabled(true);
                    shell.setProperty("script", "io.metersphere.api.jmeter.JMeterVars.addVars(prev.hashCode(),vars," + "\"" + extract.toString() + "\"" + ");");
                    node.add(shell);
                }*/
            }

            if (node != null) {
                addJSR223PostProcessor(node);
            }
        }
    }

}
