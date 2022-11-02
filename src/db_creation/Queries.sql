-- 1.1 select all the countries (to let user know the country's spelling in our system)
select countryName from Country;

-- 1.2 select all the disciplines (to let user know how many/the discipline's spelling in our system) 
select discipline from discipline;

-- 1.3 select all athletes who are from the same country (i.e., china) and play the same 'discipline' (i.e., table tennis)
-- Easy
select a.name, c.countryName, d.discipline from athletes a
join discipline d on a.discipline_code = d.discipline_code
join country c on a.country_code = c.country_code
where d.discipline = 'Table Tennis' and c.countryName = "People's Republic of China";

-- 1.4 Find the oldest/youngest athelete that participated in a 'specific' discipline (easy)
select name, min(birth_date) as birth_date, discipline from athletes natural join discipline
where birth_date != '' and discipline = 'Table Tennis';

select name, max(birth_date) as birth_date, discipline from athletes natural join discipline
where birth_date != '' and discipline = 'Table Tennis';

-- 1.5 Retrieve the name of all events given the discipline
select DISTINCT w.event, d.discipline from wins w natural join discipline d
where d.discipline = 'Table Tennis'; 

-- 1.6 Find the top ten women that acheive the most medals, list their discipline and country
-- should I do in different discipline? Yes

SELECT name, count(*) as medalCount, d.discipline, countryName
from athletes a
join discipline d on d.discipline_code = a.discipline_code
join country c on a.country_code = c.country_code
join wins w on a.name = w.athlete_name
WHERE a.gender = "Female"
GROUP by w.athlete_name
ORDER by medalCount DESC
LIMIT 10

------------------------------------------------
-- 1.7 Find the youngest athlete of each country, sport they play, and medal they obtained
-- extra: show their age (by the time of the Olympics).

SELECT a.short_name, max(birth_date) as birthyear, countryName, 
	d.discipline as sport, medal_type as medal
FROM discipline d
join athletes a on d.discipline_code = a.discipline_code
join country c on a.country_code = c.country_code
join wins w on a.name = w.athlete_name
join medal m on w.medal_code = m.medal_code
GROUP by a.country_code

-------------------------------------------------
-- 1.8 Show rank and the number of medal of every republic country

SELECT rank, countryName as country, sum(GoldMedal+SilverMedal+BronzeMedal) as totalMedal
FROM country
join summary on country.country_code = summary.country_code
WHERE countryName like '%republic%'
GROUP by rank
ORDER by rank 

-- 2.0 Summarize the number of atheletes each country sends.

SELECT c.countryName, count(a.name) as numOfAthletes
from country c natural join athletes a
group by a.country_code
order by numOfAthletes desc;

-- 2.1 Summarize the number of athletes per discipline
select d.discipline, count(a.name) as numOfAthletes
from discipline d natural join athletes a
group by d.discipline_code
order by numOfAthletes desc;

------------------------------------------------
-- 2.3 Summarize the number of athletes each country sends categorizing into discipline
-- extra :? and medals
-- extra' :? how to get a total of numberOfAthlete per country

SELECT countryName, discipline, count(a.name) as numberOfAthletes
FROM country c 
join athletes a on a.country_code = c.country_code
join discipline d on a.discipline_code = d.discipline_code
GROUP by countryName, discipline

------------------------------------------------
-- 2.4 Retrieve the number of medals of athletes in a given country and discipline 
SELECT w.athlete_name, count(w.event) as numOfMedals
FROM country c 
natural join wins w
natural join discipline d 
join athletes a on a.name = w.athlete_name
group by a.name
having c.country_code = 'CHN' and discipline = 'Badminton';

-------------------------------------------------
-- 2.5 Name the athletes that won gold medal and his coach (same discipline, same country)
-- note 1 athlete could win gold in several events
-- doesn't work cause it will always include all the coach that "linked" with a particular athlete. 
-- disable min & group by to see

SELECT a.name as athlete, c.name as coach, a.country_code, d.discipline, function 
from athletes a
join coach c on d.discipline = c.discipline AND c.country_code = a.country_code
join discipline d on a.discipline_code = d.discipline_code
--where a.name = "ABALO Luc"
where a.name in (
	SELECT DISTINCT ai.name 
	from athletes ai join wins on ai.name = wins.athlete_name
	join medal on wins.medal_code = medal.medal_code
	where medal_type = "Gold Medal"
) and c.function = 'Head Coach' and c.discipline = 'Football' and c.country_code='BRA';
--group by a.name

-- count GoldMedal
select count(*) from wins where medal_code = 1;

-- error
SELECT name,count(coach.discipline) c from coach where c >= 2 group by name;


select * from coach c where c.country_code = 'CHN' and c.discipline = 'Table Tennis';

--------------------------------------------------
-- 2.6 Retrieve the discipline that is most popular, based on the number of countries play it 
-- extra :( show the athlete that won the gold medal

SELECT d.discipline, count(DISTINCT c.country_code) as numOfCountry
FROM discipline d
join athletes a on d.discipline_code  = a.discipline_code
join country c on a.country_code = c.country_code
GROUP by d.discipline_code
ORDER by numOfCountry

-- attempt for extra 
SELECT d.discipline, count(DISTINCT c.country_code) as ccount--, medal_type as medal, countryName as country
from discipline d
join wins w on d.discipline_code = w.discipline_code
join country c on w.country_code = c.country_code
-- join medal m on w.medal_code = m.medal_code
GROUP by d.discipline
ORDER by ccount


-- 3.1 List all countries that participate in every discipline. (Hard)
select DISTINCT c1.countryName from country c1 natural join athletes a1 where not EXISTS(
	select discipline_code from discipline EXCEPT
		select discipline_code from discipline natural join country c2 natural join athletes a2 
		where c1.countryName = c2.countryName 
)

		
------------------------------------------------------------------------------------------------------
-- 3.2 List all the athletes of a country, which have medals in every event of a given discipline. (Hard)
-- BDM - badminton, TTE - table tennis (China CHN)
select w.athlete_name, c.countryName, w.event, m.medal_type from wins w 
natural join country c 
natural join discipline d 
natural join medal m
where discipline = 'Table Tennis' and NOT EXISTS(
	select event from wins natural join discipline where discipline='Table Tennis' EXCEPT
		select w1.event from wins w1 
		natural join country c1
		natural join discipline d1 
		natural join medal m1 
		where d1.discipline='Table Tennis' and c.countryName = c1.countryName
);










