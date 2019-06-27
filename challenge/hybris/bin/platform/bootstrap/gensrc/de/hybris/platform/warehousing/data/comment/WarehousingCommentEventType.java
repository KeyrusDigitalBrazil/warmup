package de.hybris.platform.warehousing.data.comment;
 
public enum WarehousingCommentEventType   
{
 
    /** <i>Generated enum value</i> for <code>WarehousingCommentEventType.CANCEL_ORDER_COMMENT("orderCancellationComment", "Order Cancellation Comment", "orderCancellationEvent", "Order Cancellation Event")</code> value defined at extension <code>warehousing</code>. */
    CANCEL_ORDER_COMMENT("orderCancellationComment", "Order Cancellation Comment", "orderCancellationEvent", "Order Cancellation Event") ,  
    /** <i>Generated enum value</i> for <code>WarehousingCommentEventType.CANCEL_CONSIGNMENT_COMMENT("consignmentCancellationComment", "Consignment Cancellation Comment", "consignmentCancellationEvent", "Consignment Cancellation Event")</code> value defined at extension <code>warehousing</code>. */
    CANCEL_CONSIGNMENT_COMMENT("consignmentCancellationComment", "Consignment Cancellation Comment", "consignmentCancellationEvent", "Consignment Cancellation Event") ,  
    /** <i>Generated enum value</i> for <code>WarehousingCommentEventType.REALLOCATE_CONSIGNMENT_COMMENT("consignmentReallocationComment", "Consignment Reallocation Comment", "consignmentReallocationEvent", "Consignment Reallocation Event")</code> value defined at extension <code>warehousing</code>. */
    REALLOCATE_CONSIGNMENT_COMMENT("consignmentReallocationComment", "Consignment Reallocation Comment", "consignmentReallocationEvent", "Consignment Reallocation Event") ,  
    /** <i>Generated enum value</i> for <code>WarehousingCommentEventType.INVENTORY_ADJUSTMENT_COMMENT("inventoryAdjustmentComment", "Inventory Adjustment Comment", "inventoryAdjustmentEvent", "Inventory Adjustment Event")</code> value defined at extension <code>warehousing</code>. */
    INVENTORY_ADJUSTMENT_COMMENT("inventoryAdjustmentComment", "Inventory Adjustment Comment", "inventoryAdjustmentEvent", "Inventory Adjustment Event") ,  
    /** <i>Generated enum value</i> for <code>WarehousingCommentEventType.CREATE_ASN_COMMENT("asnCreationComment", "Asn Creation Comment", "asnCreationEvent", "Asn Creation Event")</code> value defined at extension <code>warehousing</code>. */
    CREATE_ASN_COMMENT("asnCreationComment", "Asn Creation Comment", "asnCreationEvent", "Asn Creation Event");  
 
    final private String commentTypeCode;
    final private String commentTypeName;
    final private String componentCode;
    final private String componentName;
 
    private WarehousingCommentEventType(String commentTypeCode, String commentTypeName, String componentCode, String componentName) {
        
		this.commentTypeCode = commentTypeCode;
		this.commentTypeName = commentTypeName;
		this.componentCode = componentCode;
		this.componentName = componentName;
    }
 
	public String getComponentCode()
	{
		return componentCode;
	}

	public String getComponentName()
	{
		return componentName;
	}

	public String getCommentTypeCode()
	{
		return commentTypeCode;
	}

	public String getCommentTypeName()
	{
		return commentTypeName;
	}
 
}