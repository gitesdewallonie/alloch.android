package be.gitesdewallonie.allochambresdhotes.classes;

import java.io.Serializable;

public class Place implements Serializable {
	public int two_person_bed;
	public String name;
	public int[] classification;
	public int child_bed;
	public String price;
	public int capacity_max;
	public int room_number;
	public String thumb;
	public double longitude;
	public OWN owner;
	public int capacity_min;
	public ADDRESS address;
	public double latitude;
	public String distribution;
	public String[] photos;
	public String type;
	public int additionnal_bed;
	public int one_person_bed;
	public String description;
}
