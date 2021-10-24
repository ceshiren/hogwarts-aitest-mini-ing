package com.hogwartsmini.demo.common.jmeter;

import lombok.extern.slf4j.Slf4j;
import org.apache.jmeter.engine.JMeterEngine;
import org.apache.jmeter.engine.JMeterEngineException;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jorphan.collections.HashTree;

@Slf4j
public class LocalRunner {
    private HashTree jmxTree;

    /**
     *  通过有参构造方法传入测试脚本jmxTree
     * @param jmxTree
     */
    public LocalRunner(HashTree jmxTree) {
        this.jmxTree = jmxTree;
    }

    /**
     *  我们这里建议改成私有private更严谨些，因为我们没有停止运行方法
     */
    public LocalRunner() {
    }

    /**
     *  运行jmeter引擎，思考一下，report有什么作用？
     * @param report
     */
    public void run(String report) {
        JMeterEngine engine = new StandardJMeterEngine();
        engine.configure(jmxTree);
        try {
            engine.runTest();
        } catch (JMeterEngineException e) {
            engine.stopTest(true);
        }
    }
}
