package com.wfa.parser.spi.impl;

import com.wfa.parser.spi.api.IComponent;

public class ComponentBean implements IComponent{
	private String componentName;
	private String componentType;
	private String componentSource;
	
	public ComponentBean(String componentName, String componentType, String componentSource) {
		this.componentName = componentName;
		this.componentSource  = componentSource;
		this.componentType = componentType;
	}
	
	public String getComponentName() {
		return this.componentName;
	}
	
	public String getComponentSource() {
		return this.componentSource;
	}
	
	public String getComponentType() {
		return this.componentType;
	}
}
