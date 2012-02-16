package org.geomajas.sld.client.model;

import org.geomajas.sld.expression.ExpressionInfo;
import org.geomajas.sld.expression.LiteralTypeInfo;
import org.geomajas.sld.expression.PropertyNameInfo;
import org.geomajas.sld.filter.BinaryComparisonOpTypeInfo;
import org.geomajas.sld.filter.ComparisonOpsTypeInfo;
import org.geomajas.sld.filter.FilterTypeInfo;
import org.geomajas.sld.filter.LogicOpsTypeInfo;
import org.geomajas.sld.filter.PropertyIsBetweenTypeInfo;
import org.geomajas.sld.filter.PropertyIsEqualToInfo;
import org.geomajas.sld.filter.PropertyIsGreaterThanInfo;
import org.geomajas.sld.filter.PropertyIsGreaterThanOrEqualToInfo;
import org.geomajas.sld.filter.PropertyIsLessThanInfo;
import org.geomajas.sld.filter.PropertyIsLessThanOrEqualToInfo;
import org.geomajas.sld.filter.PropertyIsLikeTypeInfo;
import org.geomajas.sld.filter.PropertyIsNotEqualToInfo;
import org.geomajas.sld.filter.PropertyIsNullTypeInfo;
import org.geomajas.sld.filter.UnaryLogicOpTypeInfo;

public class FilterModelImpl implements FilterModel {

	private String propertyName;

	private String propertyValue;

	private String lowerValue;

	private String upperValue;

	private String patternMatchingWildCard;

	private String patternMatchingSingleChar;

	private String patternMatchingEscape;

	private FilterTypeInfo filterTypeInfo;

	private OperatorType operatorType;
	
	public FilterModelImpl() {		
	}

	public FilterModelImpl(FilterTypeInfo filterTypeInfo) {
		this.filterTypeInfo = filterTypeInfo;
		parseFilter(filterTypeInfo);
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getLowerValue() {
		return lowerValue;
	}

	public void setLowerValue(String lowerValue) {
		this.lowerValue = lowerValue;
	}

	public String getUpperValue() {
		return upperValue;
	}

	public void setUpperValue(String upperValue) {
		this.upperValue = upperValue;
	}

	public void setOperatorType(OperatorType operatorType) {
		this.operatorType = operatorType;
	}

	public OperatorType getOperatorType() {
		return operatorType;
	}

	public String getPatternMatchingWildCard() {
		return patternMatchingWildCard;
	}

	public void setPatternMatchingWildCard(String patternMatchingWildCard) {
		this.patternMatchingWildCard = patternMatchingWildCard;
	}

	public String getPatternMatchingSingleChar() {
		return patternMatchingSingleChar;
	}

	public void setPatternMatchingSingleChar(String patternMatchingSingleChar) {
		this.patternMatchingSingleChar = patternMatchingSingleChar;
	}

	public String getPatternMatchingEscape() {
		return patternMatchingEscape;
	}

	public void setPatternMatchingEscape(String patternMatchingEscape) {
		this.patternMatchingEscape = patternMatchingEscape;
	}

	public FilterTypeInfo getFilterTypeInfo() {
		return filterTypeInfo;
	}
	
	public boolean isValid() {
		// TODO: check why between is special case ?
		if (filterTypeInfo.ifComparisonOps()) {

			ComparisonOpsTypeInfo op = filterTypeInfo.getComparisonOps();

			if (op.getClass().equals(PropertyIsLikeTypeInfo.class)) {

			} else if (op.getClass().equals(PropertyIsEqualToInfo.class)) {

			} else if (op.getClass().equals(PropertyIsNotEqualToInfo.class)) {

			} else if (op.getClass().equals(PropertyIsGreaterThanInfo.class)) {

			} else if (op.getClass().equals(PropertyIsGreaterThanOrEqualToInfo.class)) {

			} else if (op.getClass().equals(PropertyIsLessThanInfo.class)) {

			} else if (op.getClass().equals(PropertyIsLessThanOrEqualToInfo.class)) {

			} else if (op.getClass().equals(PropertyIsBetweenTypeInfo.class)) {
				ExpressionInfo expressionInfo = ((PropertyIsBetweenTypeInfo) op).getExpression();
				if (expressionInfo.getClass().equals(PropertyNameInfo.class)) {
				} else {
					return false; /* ABORT */
				}
			} else if (op.getClass().equals(PropertyIsNullTypeInfo.class)) {
			} else {
				return false;
			}
		} else if (filterTypeInfo.ifLogicOps()) {
			LogicOpsTypeInfo op = filterTypeInfo.getLogicOps();
			/** Only the NOT operator is (partially) supported **/
			if (op.getClass().equals(UnaryLogicOpTypeInfo.class)) {
				UnaryLogicOpTypeInfo info = (UnaryLogicOpTypeInfo) op;
				if (info.ifComparisonOps()) {
					ComparisonOpsTypeInfo innerOp = info.getComparisonOps();

					if (innerOp.getClass().equals(PropertyIsNullTypeInfo.class)) {
					} else if (innerOp.getClass().equals(PropertyIsLikeTypeInfo.class)) {
					} else if (innerOp.getClass().equals(PropertyIsBetweenTypeInfo.class)) {
						ExpressionInfo expressionInfo = ((PropertyIsBetweenTypeInfo) innerOp).getExpression();

						if (expressionInfo.getClass().equals(PropertyNameInfo.class)) {
						} else {
							return false; 
						}
					}

				} else {
					return false;
				}
			}
		}
		return true;
	}


	private void parseFilter(FilterTypeInfo filterTypeInfo) {
		if (filterTypeInfo.ifComparisonOps()) {

			ComparisonOpsTypeInfo op = filterTypeInfo.getComparisonOps();

			if (op.getClass().equals(PropertyIsLikeTypeInfo.class)) {
				operatorType = OperatorType.PROPERTY_IS_LIKE;
				propertyName = ((PropertyIsLikeTypeInfo) op).getPropertyName().getValue();
				propertyValue = ((PropertyIsLikeTypeInfo) op).getLiteral().getValue();
				patternMatchingWildCard = ((PropertyIsLikeTypeInfo) op).getWildCard();
				patternMatchingSingleChar = ((PropertyIsLikeTypeInfo) op).getSingleChar();
				patternMatchingEscape = ((PropertyIsLikeTypeInfo) op).getEscape();
			} else if (op.getClass().equals(PropertyIsEqualToInfo.class)) {
				operatorType = OperatorType.PROPERTY_IS_EQUAL_TO;
				processBinaryComparisonOp((BinaryComparisonOpTypeInfo) op);

			} else if (op.getClass().equals(PropertyIsNotEqualToInfo.class)) {
				operatorType = OperatorType.PROPERTY_IS_NOT_EQUAL;
				processBinaryComparisonOp((BinaryComparisonOpTypeInfo) op);

			} else if (op.getClass().equals(PropertyIsGreaterThanInfo.class)) {
				operatorType = OperatorType.PROPERTY_IS_GREATER_THAN;
				processBinaryComparisonOp((BinaryComparisonOpTypeInfo) op);

			} else if (op.getClass().equals(PropertyIsGreaterThanOrEqualToInfo.class)) {
				operatorType = OperatorType.PROPERTY_IS_GREATER_THAN_OR_EQUAL;
				processBinaryComparisonOp((BinaryComparisonOpTypeInfo) op);

			} else if (op.getClass().equals(PropertyIsLessThanInfo.class)) {
				operatorType = OperatorType.PROPERTY_IS_LESS_THAN;
				processBinaryComparisonOp((BinaryComparisonOpTypeInfo) op);

			} else if (op.getClass().equals(PropertyIsLessThanOrEqualToInfo.class)) {
				operatorType = OperatorType.PROPERTY_IS_LESS_THAN_OR_EQUAL;
				processBinaryComparisonOp((BinaryComparisonOpTypeInfo) op);

			} else if (op.getClass().equals(PropertyIsBetweenTypeInfo.class)) {
				operatorType = OperatorType.PROPERTY_IS_BETWEEN;

				ExpressionInfo expressionInfo = ((PropertyIsBetweenTypeInfo) op).getExpression();

				if (expressionInfo.getClass().equals(PropertyNameInfo.class)) {
					propertyName = expressionInfo.getValue();
				}

				ExpressionInfo lowerBoundaryExpression = ((PropertyIsBetweenTypeInfo) op).getLowerBoundary()
						.getExpression();
				lowerValue = lowerBoundaryExpression.getValue();

				ExpressionInfo upperBoundaryExpression = ((PropertyIsBetweenTypeInfo) op).getUpperBoundary()
						.getExpression();
				upperValue = upperBoundaryExpression.getValue();

			} else if (op.getClass().equals(PropertyIsNullTypeInfo.class)) {
				operatorType = OperatorType.PROPERTY_IS_NULL;
				propertyName = ((PropertyIsNullTypeInfo) op).getPropertyName().getValue();
			}
		} else if (filterTypeInfo.ifLogicOps()) {
			LogicOpsTypeInfo op = filterTypeInfo.getLogicOps();
			/** Only the NOT operator is (partially) supported **/
			if (op.getClass().equals(UnaryLogicOpTypeInfo.class)) {
				UnaryLogicOpTypeInfo info = (UnaryLogicOpTypeInfo) op;
				if (info.ifComparisonOps()) {
					ComparisonOpsTypeInfo innerOp = info.getComparisonOps();

					if (innerOp.getClass().equals(PropertyIsNullTypeInfo.class)) {
						operatorType = OperatorType.PROPERTY_IS_NOT_NULL;
						propertyName = ((PropertyIsNullTypeInfo) innerOp).getPropertyName().getValue();
					} else if (innerOp.getClass().equals(PropertyIsLikeTypeInfo.class)) {
						operatorType = OperatorType.PROPERTY_IS_NOT_LIKE;
						propertyName = ((PropertyIsLikeTypeInfo) innerOp).getPropertyName().getValue();
						propertyValue = ((PropertyIsLikeTypeInfo) innerOp).getLiteral().getValue();
						patternMatchingWildCard = ((PropertyIsLikeTypeInfo) innerOp).getWildCard();
						patternMatchingSingleChar = ((PropertyIsLikeTypeInfo) innerOp).getSingleChar();
						patternMatchingEscape = ((PropertyIsLikeTypeInfo) innerOp).getEscape();

					} else if (innerOp.getClass().equals(PropertyIsBetweenTypeInfo.class)) {
						operatorType = OperatorType.PROPERTY_IS_NOT_BETWEEN;
						ExpressionInfo expressionInfo = ((PropertyIsBetweenTypeInfo) innerOp).getExpression();

						if (expressionInfo.getClass().equals(PropertyNameInfo.class)) {
							propertyName = expressionInfo.getValue();
						}

						ExpressionInfo lowerBoundaryExpression = ((PropertyIsBetweenTypeInfo) innerOp)
								.getLowerBoundary().getExpression();
						lowerValue = lowerBoundaryExpression.getValue();

						ExpressionInfo upperBoundaryExpression = ((PropertyIsBetweenTypeInfo) innerOp)
								.getUpperBoundary().getExpression();
						upperValue = upperBoundaryExpression.getValue();
					}
				}

			}
		}

	}

	private void processBinaryComparisonOp(BinaryComparisonOpTypeInfo op) {
		for (ExpressionInfo expressionInfo : op.getExpressionList()) {

			if (expressionInfo.getClass().equals(PropertyNameInfo.class)) {
				propertyName = expressionInfo.getValue();
			} else if (expressionInfo.getClass().equals(LiteralTypeInfo.class)) {
				propertyValue = expressionInfo.getValue();
			}
		}
	}

}
