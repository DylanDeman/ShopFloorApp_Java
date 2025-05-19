package util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProductionStatusConverter implements AttributeConverter<ProductionStatus, String>
{

	@Override
	public String convertToDatabaseColumn(ProductionStatus status)
	{
		if (status == null)
		{
			return null;
		}
		return status.name(); // Slaat op als "DRAAIT", "STOPPED", etc.
	}

	@Override
	public ProductionStatus convertToEntityAttribute(String dbData)
	{
		if (dbData == null)
		{
			return null;
		}
		return ProductionStatus.valueOf(dbData); // Converteer "DRAAIT" naar MachineStatus.DRAAIT
	}
}