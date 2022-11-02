"""
Created on Sat Dec 11 16:50:00 2021

@author: Daniel Mai
"""
import sqlite3

# Modify these variables depending on local filepaths
fpath1 = "athletes.csv"
fpath2 = "country.csv"
fpath3 = "discipline.csv"
fpath4 = "medal.csv"
fpath5 = 'coach.csv'
fpath6 = 'summary.csv'
fpath7 = 'officials.csv'
fpath8 = 'supports.csv'
fpath9 = 'wins.csv'    

class MyDatabase:
    def __init__(self,create=True):
        self.cur = None
        try:
            # Open a cursor to perform database operations
            self.con = sqlite3.connect("db_project.db")
            self.cur = self.con.cursor()
            if create:
                self.createTables()
                self.readInData()
                
                # Make the changes to the database persistent
                self.con.commit()
        except Exception as ex:
            print(ex)
            
    def close(self):
        self.con.close()
        
    def createTables(self):
        try:
            # Execute commands: these create new tables
            
            self.cur.execute('''CREATE TABLE athletes ( 
                                    name VARCHAR(50) primary key, 
                                    short_name VARCHAR(20), 
                                    gender VARCHAR(6),
                                    birth_date VARCHAR(15),
                                    country_code VARCHAR(5),
                                    discipline_code VARCHAR(10),
                                    foreign key (country_code) references Country(country_code),
                                    foreign key (discipline_code) references Country(discipline_code))''')
            
            self.cur.execute('''CREATE TABLE country (                                     
                                    countryName VARCHAR(40),
                                    country_code VARCHAR(5) primary key)''')
            
            self.cur.execute('''CREATE TABLE discipline (                                    
                                    discipline VARCHAR(40),
                                    discipline_code VARCHAR(3) primary key)''')      
            
            self.cur.execute('''CREATE TABLE medal ( 
                                    medal_type VARCHAR(15), 
                                    medal_code integer primary key)''')       
            
            self.cur.execute('''CREATE TABLE coach ( 
                                    name VARCHAR(30) primary key, 
                                    gender VARCHAR(5),
                                    birth_date VARCHAR(15),
                                    country_code VARCHAR(5),
                                    discipline VARCHAR(40),
                                    function VARCHAR(20),
                                    foreign key (country_code) references Country(country_code),
                                    foreign key (discipline) references Country(discipline))''')       
            
            self.cur.execute('''CREATE TABLE summary ( 
                                    Rank integer primary key, 
                                    country_code VARCHAR(5),
                                    GoldMedal integer,
                                    SilverMedal integer,
                                    BronzeMedal integer)''')       
            
            self.cur.execute('''CREATE TABLE officials ( 
                                    tech_name VARCHAR(30) primary key, 
                                    gender VARCHAR(5),
                                    birth_date VARCHAR(15),
                                    country VARCHAR(40),
                                    function VARCHAR(20),
                                    foreign key (country) references Country(countryName))''')
                        
            self.cur.execute('''CREATE TABLE supports ( 
                                    name VARCHAR(30),
                                    discipline VARCHAR(40),
                                    primary key(name, discipline),
                                    foreign key (name) references Technical(tech_name),
                                    foreign key (discipline) references Country(discipline))''') 
            
            self.cur.execute('''CREATE TABLE wins ( 
                                    medal_code integer, 
                                    athlete_name VARCHAR(50),
                                    country_code VARCHAR(5),
                                    discipline VARCHAR(40),
                                    event VARCHAR(100),
                                    foreign key (athlete_name) references athletes(name),
                                    foreign key (medal_code) references medal(medal_code))''')   
            
            self.con.commit()
            
        except Exception as ex:
            print(ex)
            
    
    def execute(self,query,*args):
        queries = query.split(";")
        results = []
        for idx, query in enumerate(queries):
            query = query.strip()
            if idx == 0 and args and (isinstance(args[0],int) or len(args[0])):
                self.cur.execute(query,args)
            else:
                self.cur.execute(query)
        if results:
            return results
    
    
    def readFile(self, file_name):
        with open(file_name ,encoding="utf-8") as file:
            lines = file.readlines()
        return lines
    
    
    
    def readInData(self):
        try:
            athletes = self.readFile(fpath1)
            country = self.readFile(fpath2)
            discipline = self.readFile(fpath3)
            medal = self.readFile(fpath4)
            coach = self.readFile(fpath5)
            summary = self.readFile(fpath6)
            officials = self.readFile(fpath7)
            supports = self.readFile(fpath8)
            wins = self.readFile(fpath9)
            
            insertLine1 = "insert into athletes  (name, short_name, gender, birth_date, country_code, discipline_code) values (?, ?, ?, ?, ?, ?);"
            insertLine2 = "insert into country  (countryName, country_code) values (?, ?);"
            insertLine3 = "insert into discipline  (discipline, discipline_code) values (?, ?);"
            insertLine4 = "insert into medal  (medal_type, medal_code) values (?, ?);"
            insertLine5 = "insert into coach  (name, gender, birth_date, country_code, discipline, function) values (?, ?, ?, ?, ?, ?);"
            insertLine6 = "insert into summary  (Rank, country_code, GoldMedal, SilverMedal, BronzeMedal) values (?, ?, ?, ?, ?);"
            insertLine7 = "insert into officials  (tech_name, gender, birth_date, country, function) values (?, ?, ?, ?, ?);"
            insertLine8 = "insert into supports  (name, discipline) values (?, ?);"
            insertLine9 = "insert into wins  (medal_code, athlete_name, country_code, discipline, event) values (?, ?, ?, ?, ?);"
            
            
            for line in athletes[1:]:
                clean = line.strip().split(",")
                self.execute(insertLine1, clean[0], clean[1],clean[2], clean[3], clean[4], clean[5])
                
            for line in country[1:]:
                clean = line.strip().split(",")                
                self.execute(insertLine2, clean[0], clean[1])            

            for line in discipline[1:]:
                clean = line.strip().split(",")               
                self.execute(insertLine3, clean[0], clean[1])

            for line in medal[1:]:
                clean = line.strip().split(",")               
                self.execute(insertLine4, clean[0], clean[1])

            for line in coach[1:]:
                clean = line.strip().split(",")               
                self.execute(insertLine5, clean[0], clean[1],clean[2], clean[3], clean[4], clean[5])

            for line in summary[1:]:
                clean = line.strip().split(",")                
                self.execute(insertLine6, clean[0], clean[1],clean[2], clean[3], clean[4])

            for line in officials[1:]:
                clean = line.strip().split(",")
                self.execute(insertLine7, clean[0], clean[1],clean[2], clean[3], clean[4])

            for line in supports[1:]:
                clean = line.strip().split(",")               
                self.execute(insertLine8, clean[0], clean[1])      
                
            for line in wins[1:]:
                clean = line.strip().split(",")               
                self.execute(insertLine9, clean[0], clean[1],clean[2], clean[3], clean[4]) 
                
        except Exception as e:
            print(e)
        



def main():
    MyDatabase()

    
if __name__ == "__main__":
    main()