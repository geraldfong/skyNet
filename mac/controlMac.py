import web
import os

urls = (
    '/switchDeskLeft', 'switchDeskLeft',
    '/switchDeskRight', 'switchDeskRight',
    '/nextSlide', 'nextSlide',
    '/changeVolume', 'changeVolume'
)
app = web.application(urls, globals())

class switchDeskLeft:
  def GET(self):
    os.system("osascript -e 'tell application \"System Events\"' -e 'key down {command}' -e 'keystroke (key code 123)' -e 'key up {command}' -e 'end tell'")
    return "SUCCESS"

class switchDeskRight:
  def GET(self):
    os.system("osascript -e 'tell application \"System Events\"' -e 'key down {command}' -e 'keystroke (key code 124)' -e 'key up {command}' -e 'end tell'")
    return "SUCCESS"

class nextSlide:
  def GET(self):
    os.system("osascript -e 'tell application \"System Events\" to keystroke (key code 124)'")
    return "SUCCESS"

class changeVolume:
  def GET(self):
    volume = web.input(volume = "3").volume
    os.system("osascript -e 'set volume " + volume + "'")

    return "SUCCESS"

if __name__ == "__main__":
    app.run()
