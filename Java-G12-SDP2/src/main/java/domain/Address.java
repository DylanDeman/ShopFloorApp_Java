package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Address
{
	private String street;
	private int number;
	private int postalcode;
	private String city;
}
