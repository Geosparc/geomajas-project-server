package org.geomajas.sld.client.model;

import org.geomajas.sld.StyledLayerDescriptorInfo;

public interface SldModel extends SldGeneralInfo {

	boolean isComplete();

	String getName();

	StyledLayerDescriptorInfo getSld();

	RuleGroup getRuleGroup();

	boolean isRulesSupported();

	void synchronize();

	void copyFrom(SldModel create);

}