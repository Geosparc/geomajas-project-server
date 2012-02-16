package org.geomajas.sld.client.model;

import java.util.ArrayList;
import java.util.List;

import org.geomajas.sld.FeatureTypeStyleInfo;
import org.geomajas.sld.NamedLayerInfo;
import org.geomajas.sld.NamedLayerInfo.ChoiceInfo;
import org.geomajas.sld.NamedStyleInfo;
import org.geomajas.sld.RuleInfo;
import org.geomajas.sld.StyledLayerDescriptorInfo;
import org.geomajas.sld.UserStyleInfo;
import org.geomajas.sld.client.model.RuleModel.TypeOfRule;
import org.geomajas.sld.editor.client.GeometryType;
import org.geomajas.sld.editor.client.SldUtils;
import org.geomajas.sld.editor.client.i18n.SldEditorMessages;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class SldModelImpl implements SldModel {

	private StyledLayerDescriptorInfo sld;

	private String nameOfLayer;

	private String styleTitle;

	private GeometryType geomType; // Cannot be updated

	private RuleGroup ruleGroup;

	private SldEditorMessages messages;
	
	private RuleModelFactory ruleModelFactory;
	
	private boolean rulesSupported;

	@Inject
	public SldModelImpl(@Assisted StyledLayerDescriptorInfo sld, SldEditorMessages messages, RuleModelFactory ruleModelFactory) {
		this.messages = messages;
		this.ruleModelFactory = ruleModelFactory;
		setSld(sld);
	}
	
	/* (non-Javadoc)
	 * @see org.geomajas.sld.client.model.SldModelIntf#isComplete()
	 */
	public boolean isComplete() {
		if(!isRulesSupported()) {
			return false;
		}
		// check if all filters/rules are complete !
		for (RuleModel model : getRuleGroup().getRuleModelList()) {
			if(model.getTypeOfRule() == TypeOfRule.INCOMPLETE_RULE){
				return false;
			}
		}
		return true;
	}
	
	public void copyFrom(SldModel other) {
		setSld(other.getSld());
	}

	private void setSld(StyledLayerDescriptorInfo sld) {
		this.sld = sld;
		geomType = GeometryType.UNSPECIFIED;
		if (sld.getChoiceList().size() == 1) {
			NamedLayerInfo namedLayerInfo = sld.getChoiceList().get(0).getNamedLayer();
			if (namedLayerInfo.getChoiceList().size() == 1) {
				UserStyleInfo userStyle = namedLayerInfo.getChoiceList().get(0).getUserStyle();
				if (userStyle.getFeatureTypeStyleList().size() == 1) {
					FeatureTypeStyleInfo featureTypeStyle = userStyle.getFeatureTypeStyleList().get(0);
					ruleGroup = new RuleGroupImpl();
					String styleTitle = featureTypeStyle.getTitle();
					if (null == styleTitle) {
						ruleGroup.setTitle("groep 1");
					} else {
						ruleGroup.setTitle(styleTitle);
					}
					ruleGroup.setName(featureTypeStyle.getName());
					ruleGroup.setRuleModelList(new ArrayList<RuleModel>());
					ruleGroup.setGeomType(GeometryType.UNSPECIFIED);
					for (RuleInfo rule : featureTypeStyle.getRuleList()) {

						// Determine the rule title to be used in the model, make sure it is NOT null or ""
						String title;

						if (null != rule.getTitle() && rule.getTitle().length() > 0) {
							title = rule.getTitle();
						} else if (null != rule.getName() && rule.getName().length() > 0) {
							title = rule.getName();
						} else {
							title = messages.ruleTitleUnspecified();
						}
						RuleModel ruleModel = ruleModelFactory.create(rule);
						ruleGroup.setGeomType(ruleModel.getGeometryType()); // copy the last one (should be equal for all)
						ruleGroup.getRuleModelList().add(ruleModel);
						rulesSupported = true;
					}
				}

			}
		}
	}

	public void synchronize() {
		// retrieve the first choice
		StyledLayerDescriptorInfo.ChoiceInfo info = sld.getChoiceList().iterator().next();
		if (!info.ifNamedLayer()) {
			// warning that invalid SLD
			// SC.warn("Only SLD's with a &lt;NamedLayer&gt; element are supported.");
			return; // ABORT
		}

		// Update the name of the layer
		info.getNamedLayer().setName(getNameOfLayer());

		List<ChoiceInfo> choiceList = info.getNamedLayer().getChoiceList();
		// retrieve the first constraint
		ChoiceInfo choiceInfo = choiceList.iterator().next();


		if (choiceInfo.ifNamedStyle()) {
			// Only the name is specialized
			if (null == choiceInfo.getNamedStyle()) {
				choiceInfo.setNamedStyle(new NamedStyleInfo());
			}
			choiceInfo.getNamedStyle().setName(getStyleTitle());
		} else if (choiceInfo.ifUserStyle()) {
			choiceInfo.getUserStyle().setTitle(getStyleTitle());
			List<RuleInfo> rules = new ArrayList<RuleInfo>();
			for (RuleModel model : getRuleGroup().getRuleModelList()) {
				if(model.getTypeOfRule() != TypeOfRule.INCOMPLETE_RULE){
					rules.add(model.getRuleInfo());
				}
			}
			choiceInfo.getUserStyle().getFeatureTypeStyleList().get(0).setRuleList(rules);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.geomajas.sld.client.model.SldModelIntf#getName()
	 */
	public String getName() {
		return sld.getName();
	}

	/* (non-Javadoc)
	 * @see org.geomajas.sld.client.model.SldModelIntf#getSld()
	 */
	public StyledLayerDescriptorInfo getSld() {
		return sld;
	}

	public String getNameOfLayer() {
		return nameOfLayer;
	}

	public void setNameOfLayer(String nameOfLayer) {
		this.nameOfLayer = nameOfLayer;
	}

	public String getStyleTitle() {
		return styleTitle;
	}

	public void setStyleTitle(String styleTitle) {
		this.styleTitle = styleTitle;
	}
	
	public GeometryType getGeomType() {
		return geomType;
	}

	/* (non-Javadoc)
	 * @see org.geomajas.sld.client.model.SldModelIntf#getRuleGroup()
	 */
	public RuleGroup getRuleGroup() {
		return ruleGroup;
	}

	public void setRuleGroup(RuleGroup ruleGroup) {
		this.ruleGroup = ruleGroup;
	}
	
	/* (non-Javadoc)
	 * @see org.geomajas.sld.client.model.SldModelIntf#isRulesSupported()
	 */
	public boolean isRulesSupported() {
		return rulesSupported;
	}
	

}
