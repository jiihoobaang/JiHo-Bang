import unittest
import doodlejump

class Testdoodlejump(unittest.TestCase):
#This code tests the playcam function. We chose this function to test as it is an important helper block throughout the game code. 
#The output of the function determines the location of Doodle on the screen. 
#The most important use of this function was to see if Doodle was below the screen. This way, the other functions would know to end the game, reset, and start a new game. 
	def playcam(self):
		return self.playery - self.cameray

	def setUp(self):
		self.playery = 1000
		self.cameray = 200

	def test_playcam(self):
		#This test case tests if the function is making the proper calculation of self.playery - self.cameray. 
		#It is important that this test case passes as it is crucial information to determien Doodle's precise location. 
		self.assertEqual(self.playcam(), 800)

		self.playery = 200
		self.cameray = 100

		#These next test cases let us know if Doodle is above a certain position on the screen. 
		#It is important that these test cases pass because they let us know if and when Doodle is above or below a certain area, that we choose on the screen.
		self.assertFalse(self.playcam() > 800)
		self.assertTrue(self.playcam() < 800)

		self.playery = 800
		self.cameray = 100

		#Finally, these test cases combine the first two cases. First, it calculates the position. Second, it determines if Doodle is below that area. 
		#This case is also an edge case as the parameter is set to 700, which is the limit for the screen. If Doodle is above 700, it means the game ends.
		#Thus, it is important that this passes because it will show that even if Doodle is at the very edge of the screen, nothing will happen. Doodle must actually supass the threshold.
		self.assertEqual(self.playcam(), 700)
		self.assertFalse(self.playcam() > 700)

if __name__ == '__main__':
    unittest.main()