package jackson.inheritance.jpa.specification.search;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.Specification;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type")
@JsonSubTypes({
		@Type(value = Simple.class, name = "simple"),
		@Type(value = Collection.class, name = "collection"),
		@Type(value = Range.class, name = "range"),
		@Type(value = Time.class , name = "time"),
		@Type(value = Complex.class, name = "complex"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractSearchViewModel<T> {
	@NotEmpty
	protected String field;

	public abstract Specification<T> action();

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

}
