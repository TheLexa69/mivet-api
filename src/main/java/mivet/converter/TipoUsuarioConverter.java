package mivet.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import mivet.enums.TipoUsuario;

@Converter(autoApply = true)
public class TipoUsuarioConverter implements AttributeConverter<TipoUsuario, String> {

    @Override
    public String convertToDatabaseColumn(TipoUsuario attribute) {
        return attribute == null ? null : attribute.name(); // guarda como "privado"
    }

    @Override
    public TipoUsuario convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : TipoUsuario.valueOf(dbData.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Valor inválido para TipoUsuario: " + dbData);
        }
    }
}
