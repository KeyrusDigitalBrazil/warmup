package de.hybris.platform.warehousing.data.sourcing;
 
public enum SourcingFactorIdentifiersEnum   
{
 
    /** <i>Generated enum value</i> for <code>SourcingFactorIdentifiersEnum.Distance("warehousing.factor.distance.weight")</code> value defined at extension <code>warehousing</code>. */
    DISTANCE("WAREHOUSING.FACTOR.DISTANCE.WEIGHT") ,  
    /** <i>Generated enum value</i> for <code>SourcingFactorIdentifiersEnum.Allocation("warehousing.factor.allocation.weight")</code> value defined at extension <code>warehousing</code>. */
    ALLOCATION("WAREHOUSING.FACTOR.ALLOCATION.WEIGHT") ,  
    /** <i>Generated enum value</i> for <code>SourcingFactorIdentifiersEnum.Priority("warehousing.factor.priority.weight")</code> value defined at extension <code>warehousing</code>. */
    PRIORITY("WAREHOUSING.FACTOR.PRIORITY.WEIGHT") ,  
    /** <i>Generated enum value</i> for <code>SourcingFactorIdentifiersEnum.Score("warehousing.factor.score.weight")</code> value defined at extension <code>warehousing</code>. */
    SCORE("WAREHOUSING.FACTOR.SCORE.WEIGHT");  
 
    final private String value;
 
    private SourcingFactorIdentifiersEnum(String value) {
        this.value = value;
    }
 
    public String getValue() {
        return value;
    }
 
}