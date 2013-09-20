package gov.nist.toolkit.wsseTool.engine;

import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

public class MyRunnerBuilder extends RunnerBuilder{

	TestData data;
	
	public MyRunnerBuilder(TestData data){
		this.data = data;
	}
	
	
	@Override
	public Runner runnerForClass(Class<?> testClass) throws Throwable {
		return new MyRunnerWithOrder(testClass, data);
	}

}
