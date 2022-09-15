import pygame #this is the package pygame which is importing here
from pygame.locals import * # here some classes accessed from pygame
import sys
import random

#Below I have edited the program to include everything in the assets folder
#It is a user-defined prototype from which objects are created.
class DoodleJump:
    #Used as a constructor and used to assign values of the class.
    def __init__(self):
        #It actually creates an instance of the pygame
        self.screen = pygame.display.set_mode((800, 600))
        #It is is a file that is loaded into the pygame with the file name and the extension listed from its directory
        self.green = pygame.image.load("assets/green.png").convert_alpha()
        pygame.font.init()
        #Initializes the font module inside the pygame
        pygame.font.init()
        #Represents the initial score of the game which is zero
        self.score = 0
        #Represents the list that will increment scores of the game
        self.scores = []
        #Sets the fonts in the game to Arial and tp size  25
        self.font = pygame.font.SysFont("Arial", 25)
        #Initializes image files to the pygame
        self.blue = pygame.image.load("assets/blue.png").convert_alpha()
        self.red = pygame.image.load("assets/red.png").convert_alpha()
        self.red_1 = pygame.image.load("assets/red_1.png").convert_alpha()
        self.playerRight = pygame.image.load("assets/right.png").convert_alpha()
        self.playerRight_1 = pygame.image.load("assets/right_1.png").convert_alpha()
        self.playerLeft = pygame.image.load("assets/left.png").convert_alpha()
        self.playerLeft_1 = pygame.image.load("assets/left_1.png").convert_alpha()
        self.spring = pygame.image.load("assets/spring.png").convert_alpha()
        self.spring_1 = pygame.image.load("assets/spring_1.png").convert_alpha()
        #The fixed start point of the game 
        self.direction = 0
        #Sets the surface wrapper of player X to 400 to draw images on 
        self.playerx = 400
        #Sets the surface wrapper of player Y to 400 to draw images on 
        self.playery = 400
        #Platform initialization for players interaction with the game
        self.platforms = [[400, 500, 0, 0]]
        #List representing springs used in connecting particles to simulate complex soft bodies
        self.springs = []
        #Initial values for the player's camera, jump, gravity and movement. 
        self.cameray = 0
        self.jump = 0
        self.gravity = 0
        self.xmovement = 0

#This function will determine Doodle's location on the screen. Helps other functions to see if game ends. 
    def playcam(self):
        return self.playery - self.cameray

#Updates the state of the player in accordance to the action of an event. If the player does not jump then the gravity is incremented and the reverse sees the gravity decremented. 
#This function updates the current state of the set keys used in the pygame. The input parameter of this function is the current object and all it values are accessed and updated within this function.
#The updated values includes the position and the movement of the players in regard to the action and the current state of the current object.
#The returned values of this function is the new values obtained from the obtained keys in the pygame.
    def updatePlayer(self):
        if not self.jump:
            self.playery += self.gravity
            self.gravity += 1
        elif self.jump:
            self.playery -= self.jump
            self.jump -= 1
        key = pygame.key.get_pressed()
        if key[K_RIGHT]:
            if self.xmovement < 10:
                self.xmovement += 1
            self.direction = 0

        elif key[K_LEFT]:
            if self.xmovement > -10:
                self.xmovement -= 1
            self.direction = 1
        else:
            if self.xmovement > 0:
                self.xmovement -= 1
            elif self.xmovement < 0:
                self.xmovement += 1
        if self.playerx > 850:
            self.playerx = -50
        elif self.playerx < -50:
            self.playerx = 850
        self.playerx += self.xmovement
        if self.playcam() <= 200:
            self.cameray -= 10
        if not self.direction:
            if self.jump:
                self.screen.blit(self.playerRight_1, (self.playerx, self.playcam()))
            else:
                self.screen.blit(self.playerRight, (self.playerx, self.playcam()))
        else:
            if self.jump:
                self.screen.blit(self.playerLeft_1, (self.playerx, self.playcam()))
            else:
                self.screen.blit(self.playerLeft, (self.playerx, self.playcam()))

#This function has the current object as its input parameter.
#It accepts the object and interprets the values of the current state and moves the players accordingly in regard to the updated vales from the updatePlayer() function.
#The output of this function sees the positions of the players changed on a single event on the pygame.
    def updatePlatforms(self):
        for p in self.platforms:
            rect = pygame.Rect(p[0], p[1], self.green.get_width() - 10, self.green.get_height())
            player = pygame.Rect(self.playerx, self.playery, self.playerRight.get_width() - 10, self.playerRight.get_height())
            if rect.colliderect(player) and self.gravity and self.playery < (p[1] - self.cameray):
                if p[2] == 1 or p[2] == 0:
                    self.jump = 15
                    self.gravity = 0
                else:
                    p[-1] = 1
            if p[2] == 1:
                if p[-1] == 1:
                    p[0] += 5
                    if p[0] > 550:
                        p[-1] = 0
                else:
                    p[0] -= 5
                    if p[0] <= 0:
                        p[-1] = 1

#This function sets a platform which is used to represent the sprites under which the players interact with in the pygame.
#It takes in the current self object values to make the platform. The output product is the platform whose dimensions and look are in accordance to the values of the current object values.
#This creates a surface of interaction between the players.
    def drawPlatforms(self):
        for p in self.platforms:
            check = self.platforms[1][1] - self.cameray
            if check > 600:
                platform = random.randint(0, 1000)
                if platform < 800:
                    platform = 0
                elif platform < 900:
                    platform = 1
                else:
                    platform = 2

                self.platforms.append([random.randint(0, 700), self.platforms[-1][1] - 50, platform, 0])
                coords = self.platforms[-1]
                check = random.randint(0, 1000)
                if check > 900 and platform == 0:
                    self.springs.append([coords[0], coords[1] - 25, 0])
                self.platforms.pop(0)
                self.score += 100
            if p[2] == 0:
                self.screen.blit(self.green, (p[0], p[1] - self.cameray))
            elif p[2] == 1:
                self.screen.blit(self.blue, (p[0], p[1] - self.cameray))
            elif p[2] == 2:
                if not p[3]:
                    self.screen.blit(self.red, (p[0], p[1] - self.cameray))
                else:
                    self.screen.blit(self.red_1, (p[0], p[1] - self.cameray))


        for spring in self.springs:
            if spring[-1]:
                self.screen.blit(self.spring_1, (spring[0], spring[1] - self.cameray))
            else:
                self.screen.blit(self.spring, (spring[0], spring[1] - self.cameray))
            if pygame.Rect(spring[0], spring[1], self.spring.get_width(), self.spring.get_height()).colliderect(pygame.Rect(self.playerx, self.playery, self.playerRight.get_width(), self.playerRight.get_height())):
                self.jump = 50
                self.cameray -= 50

#A function that creates platforms. Depending on the random number generated, it will create a red, blue, or green platform that all function uniquely.
#It receives the current object as the parameter in the pygame
    def generatePlatforms(self):
        on = 600
        while on > -100:
            x = random.randint(0,700)
            platform = random.randint(0, 1000)
            if platform < 800:
                platform = 0
            elif platform < 900:
                platform = 1
            else:
                platform = 2
            self.platforms.append([x, on, platform, 0])
            on -= 50

#Represents the graphical grids which encapsulates the pygame as an output.
#The input parameter is the self object whose values are accesed inside the function.
    def drawGrid(self):
        for x in range(80):
            pygame.draw.line(self.screen, (222,222,222), (x * 12, 0), (x * 12, 600))
            pygame.draw.line(self.screen, (222,222,222), (0, x * 12), (800, x * 12))


#This run() function is the part of the code that allows the game to star. It accepts the self object as a parameter. 
#Also included in this function is the code that allows the game to immediately restart when Doodle is off the screen as everything resets to 0. 
#Finally, it is also used to represent the scores as an ouput and it shows the highest score and the current score.
    def run(self): 
        clock = pygame.time.Clock()
        self.generatePlatforms()
        while True:
            self.screen.fill((255,255,255))
            clock.tick(60)
            for event in pygame.event.get():
                if event.type == QUIT:
                    sys.exit()
            if self.playcam() > 700:
                self.cameray = 0
                self.scores.append(self.score)
                self.scores.sort(reverse = True)
                if(len(self.scores) > 5):
                    self.scores = self.scores[:5]
                self.score = 0
                self.springs = []
                self.platforms = [[400, 500, 0, 0]]
                self.generatePlatforms()
                self.playerx = 400
                self.playery = 400
            self.drawGrid()
            self.drawPlatforms()
            self.updatePlayer()
            self.updatePlatforms()
            self.screen.blit(self.font.render('Score: '+str(self.score), -1, (0, 0, 0)), (25, 25))
            self.screen.blit(self.font.render('High scores:', -1, (0, 0, 0)), (25, 50))
            for x, score in enumerate(self.scores):
                self.screen.blit(self.font.render(str(score), -1, (0, 0, 0)), (25, 75+x*25))
            pygame.display.flip()



#To run the program, type in the code 'DoodleJump().run()' in the terminal!
